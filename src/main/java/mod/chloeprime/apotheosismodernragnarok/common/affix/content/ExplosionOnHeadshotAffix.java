package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.init.ModDamageTypes;
import dev.shadowsoffire.apotheosis.adventure.affix.*;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 爆头时伤害周围的敌人
 */
@Mod.EventBusSubscriber
public class ExplosionOnHeadshotAffix extends AbstractValuedAffix {

    public static final DynamicHolder<ExplosionOnHeadshotAffix> INSTANCE
            = AffixRegistry.INSTANCE.holder(ApotheosisModernRagnarok.loc("head_explode"));

    public static final Codec<ExplosionOnHeadshotAffix> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    ExtraCodecs.LOOT_CATEGORY_SET.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    GemBonus.VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues),
                    ExtraCodecs.COEFFICIENT_BY_RARITY.fieldOf("ranges").forGetter(a -> a.ranges))
            .apply(builder, ExplosionOnHeadshotAffix::new));

    public static final TagKey<Item> DISABLE_TAG = TagKey.create(Registries.ITEM, ApotheosisModernRagnarok.loc("affix/head_explode/disable"));

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        var percent = 100 * getValue(stack, rarity, level);
        list.accept(Component.translatable(desc(), fmt(percent)).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory category, LootRarity rarity) {
        return super.canApplyTo(stack, category, rarity) && !stack.is(DISABLE_TAG);
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
        if (event.getLogicalSide().isClient()) {
            return;
        }
        if (!(event.getHurtEntity() instanceof LivingEntity victim)) {
            return;
        }
        INSTANCE.getOptional().ifPresent(affix -> Optional.ofNullable(event.getAttacker()).ifPresent(shooter -> {
            // 获取武器stack并检查枪械id
            DamageUtils.getWeapon(shooter, event.getGunId()).ifPresent(gun ->
            Optional.ofNullable(AffixHelper.getAffixes(gun).get(INSTANCE)).ifPresent(
                    instance -> affix.onHeadshot(
                            shooter, gun, event.getGunId(),
                            victim, event.getBaseAmount(), instance)));
        }));
    }

    @SuppressWarnings("DataFlowIssue")
    public void onHeadshot(LivingEntity shooter, ItemStack weapon, ResourceLocation gunId, LivingEntity originalVictim, float bulletDamage, AffixInstance instance) {
        if (!(originalVictim.level() instanceof ServerLevel level)) {
            return;
        }
        var source = new DamageSource(shooter.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ModDamageTypes.BULLET), shooter, shooter);
        var damage = (float) (bulletDamage * getValue(weapon, instance));
        var range = ranges.getDouble(instance.rarity());

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
