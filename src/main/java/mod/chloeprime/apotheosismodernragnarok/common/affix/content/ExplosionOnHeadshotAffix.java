package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.google.gson.JsonObject;
import com.tac.guns.entity.DamageSourceProjectile;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDamageSource;
import mod.chloeprime.apotheosismodernragnarok.common.util.AffixHelper2;
import mod.chloeprime.apotheosismodernragnarok.common.util.AffixRarityConfigMap;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.AffixInstance;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.apotheosis.util.DamageSourceUtil;
import shadows.placebo.json.DynamicRegistryObject;

import java.util.function.Consumer;

/**
 * 爆头时伤害周围的敌人
 */
public class ExplosionOnHeadshotAffix extends AbstractValuedAffix {

    public static final DynamicRegistryObject<ExplosionOnHeadshotAffix> INSTANCE
            = AffixManager.INSTANCE.makeObj(ApotheosisModernRagnarok.loc("head_explode"));

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        var percent = 100 * getValue(stack, rarity, level);
        list.accept(new TranslatableComponent(desc(), fmt(percent)).withStyle(ChatFormatting.YELLOW));
    }

    public ExplosionOnHeadshotAffix(Pojo data) {
        super(AffixType.EFFECT, data);
        this.ranges = data.ranges;
    }

    @SuppressWarnings("DataFlowIssue")
    public void onHeadshot(LivingEntity originalVictim, DamageSource source, float originalDamage, AffixInstance instance) {
        if (!(source.getEntity() instanceof LivingEntity shooter) || !(originalVictim.getLevel() instanceof ServerLevel level)) {
            return;
        }
        DamageUtils.ifIsDamageFirstPart(source, originalDamage, fixedDamage -> {
            var weapon = DamageUtils.getWeapon(source);
            var damage = fixedDamage * getValue(weapon, instance);
            var range = ranges.getFloat(instance.rarity());

            // 复制 DamageSource，并设置为非爆头伤害
            var source2 = new DamageSourceProjectile(source.getMsgId(), source.getDirectEntity(), source.getEntity(), weapon);
            ((DamageSourceUtil.DmgSrcCopy) source2).copyFrom(source);
            ((ExtendedDamageSource) source2).apotheosis_modern_ragnarok$setHeadshot(false);

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
                    nearbyVictim.hurt(source2, damage);
                    var particleSpeed = 2;
                    var parEnd = nearbyVictim.position().add(0, nearbyVictim.getBbHeight() / 2, 0);
                    var dir = parEnd.subtract(parStart).normalize();
                    level.sendParticles(ParticleTypes.END_ROD, parStart.x, parStart.y, parStart.z, 0, dir.x, dir.y, dir.z, particleSpeed);
                    anyHit = true;
                }
            }
            if (anyHit) {
                var sfxPitch = shooter.getRandom().nextFloat(0.8F, 1.25F);
                level.playSound(null, originalVictim, ModContent.Sounds.HEAD_EXPLOSION.get(), originalVictim.getSoundSource(), 1, sfxPitch);
            }
        });
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

    private final AffixRarityConfigMap ranges;

    public static class Pojo extends AbstractValuedAffix.Pojo {
        public AffixRarityConfigMap ranges;
    }

    @SuppressWarnings("unused")
    public static ExplosionOnHeadshotAffix read(JsonObject obj) {
        return read(obj, ExplosionOnHeadshotAffix::new, Pojo::new, ExplosionOnHeadshotAffix::readBase);
    }

    @SuppressWarnings("unused")
    public static ExplosionOnHeadshotAffix read(FriendlyByteBuf buf) {
        return read(buf, ExplosionOnHeadshotAffix::new, Pojo::new, ExplosionOnHeadshotAffix::readBase);
    }

    public static void readBase(JsonObject obj, Pojo dataHolder) {
        AbstractValuedAffix.readBase(obj, dataHolder);
        dataHolder.ranges = AffixHelper2.readRarityConfig(obj, "range");
    }

    public static void readBase(FriendlyByteBuf buf, Pojo dataHolder) {
        AbstractValuedAffix.readBase(buf, dataHolder);
        dataHolder.ranges = buf.readMap(AffixRarityConfigMap::new, b -> LootRarity.byId(b.readUtf()), FriendlyByteBuf::readFloat);
    }

    @Override
    public JsonObject write() {
        return super.write();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeMap(ranges, (b, rarity) -> b.writeUtf(rarity.id()), FriendlyByteBuf::writeFloat);
    }
}
