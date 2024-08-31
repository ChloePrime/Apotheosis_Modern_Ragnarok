package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.entity.KnockBackModifier;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.init.ModDamageTypes;
import dev.shadowsoffire.apotheosis.adventure.affix.*;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 爆头时伤害周围的敌人
 */
@Mod.EventBusSubscriber
public class ExplosionOnHeadshotAffix extends AbstractValuedAffix {

    public static final DynamicHolder<ExplosionOnHeadshotAffix> INSTANCE = ModContent.Affix.HEAD_EXPLODE;

    public static final Codec<ExplosionOnHeadshotAffix> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    GemBonus.VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues),
                    ExtraCodecs.COEFFICIENT_BY_RARITY.fieldOf("ranges").forGetter(a -> a.ranges))
            .apply(builder, ExplosionOnHeadshotAffix::new));

    @Override
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        var rate = getValue(stack, rarity, level);
        return Component.translatable(desc(), fmtPercent(rate)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(ItemStack stack, LootRarity rarity, float level) {
        var rate = getValue(stack, rarity, level);
        var min = getValue(stack, rarity, 0);
        var max = getValue(stack, rarity, 1);
        return Component.translatable(desc(), fmtPercents(rate, min, max)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    public ExplosionOnHeadshotAffix(
            Set<LootCategory> categories,
            Map<LootRarity, StepFunction> values,
            Map<LootRarity, Double> ranges) {
        super(AffixType.ABILITY, categories, values);
        this.ranges = new Object2DoubleOpenHashMap<>(ranges);
    }

    @SubscribeEvent
    public static void onShot(EntityHurtByGunEvent.Post event) {
        DMG_CAPTURE_CACHE.remove(event.getHurtEntity());
        if (event.getLogicalSide().isClient() || !event.isHeadShot()) {
            return;
        }
        onShot0(event.getHurtEntity(), event.getAttacker(), event.getGunId(), event.getBaseAmount());
    }

    private static final Map<Entity, Float> DMG_CAPTURE_CACHE = new WeakHashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void captureAmount(EntityHurtByGunEvent.Pre event) {
        DMG_CAPTURE_CACHE.put(event.getHurtEntity(), event.getBaseAmount());
    }

    @SubscribeEvent
    public static void onShot(EntityKillByGunEvent event) {
        var amount = Optional.ofNullable(DMG_CAPTURE_CACHE.get(event.getKilledEntity()));
        DMG_CAPTURE_CACHE.remove(event.getKilledEntity());
        if (event.getLogicalSide().isClient() || !event.isHeadShot()) {
            return;
        }
        amount.ifPresent(amt -> onShot0(event.getKilledEntity(), event.getAttacker(), event.getGunId(), amt));
    }


    private static void onShot0(Entity hurtEntity, LivingEntity attacker, ResourceLocation gunId, float baseAmount) {
        if (!(hurtEntity instanceof LivingEntity victim)) {
            return;
        }
        INSTANCE.getOptional().ifPresent(affix -> Optional.ofNullable(attacker).ifPresent(shooter -> {
            // 获取武器stack并检查枪械id
            DamageUtils.getWeapon(shooter, gunId).ifPresent(
                    gun -> Optional.ofNullable(AffixHelper.getAffixes(gun).get(INSTANCE))
                            .ifPresent(instance -> affix.onHeadshot(shooter, gun, gunId, victim, baseAmount, instance)));
        }));
    }

    public void onHeadshot(LivingEntity shooter, ItemStack weapon, ResourceLocation gunId, LivingEntity originalVictim, float bulletDamage, AffixInstance instance) {
        if (!(originalVictim.level() instanceof ServerLevel level)) {
            return;
        }
        var source = new DamageSource(shooter.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ModDamageTypes.BULLET), shooter, shooter);
        var damage = (float) (bulletDamage * getValue(weapon, instance));
        var range = ranges.getDouble(instance.rarity().get());

        var parStart = originalVictim.getEyePosition();
        var targetCondition = TargetingConditions.forCombat().range(range);
        var roughBB = originalVictim.getBoundingBox().inflate(range + 4);
        var anyHit = false;
        for (LivingEntity nearbyVictim : level.getNearbyEntities(
                LivingEntity.class, targetCondition, originalVictim, roughBB)
        ) {
            if (nearbyVictim == shooter || nearbyVictim == originalVictim) {
                continue;
            }
            if (isEnemy(shooter, nearbyVictim) && shooter.canAttack(nearbyVictim)) {
                KnockBackModifier.fromLivingEntity(nearbyVictim).setKnockBackStrength(0);
                nearbyVictim.hurt(source, damage);
                var particleSpeed = 2;
                var parEnd = nearbyVictim.position().add(0, nearbyVictim.getBbHeight() / 2, 0);
                var dir = parEnd.subtract(parStart).normalize();
                level.sendParticles(ParticleTypes.END_ROD, parStart.x, parStart.y, parStart.z, 0, dir.x, dir.y, dir.z, particleSpeed);
                anyHit = true;
            }
        }
        if (anyHit) {
            var sfxPitch = Mth.lerp(shooter.getRandom().nextFloat(), 0.8F, 1.25F);
            level.playSound(null, originalVictim, ModContent.Sounds.HEAD_EXPLOSION.get(), originalVictim.getSoundSource(), 1, sfxPitch);
        }
    }

    private static boolean isEnemy(LivingEntity shooter, LivingEntity victim) {
        if (victim.getClassification(false) == MobCategory.MONSTER) {
            return true;
        }
        // 是否允许 pvp 由其他软件控制，这里不阻止
        if (shooter instanceof Player && victim instanceof Player) {
            return true;
        }
        return victim instanceof Mob mobVictim && shooter.equals(mobVictim.getTarget());
    }

    private final Object2DoubleOpenHashMap<LootRarity> ranges;

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
