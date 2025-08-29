package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 提升爆头倍率，
 * 但效果等级 >= 5 且未爆头命中时受到大量伤害并清空该 buff
 */
public class VerdantRuinEffect extends MobEffectBaseUtility {
    public static final int DEFAULT_MAX_LEVEL = 10;
    public static final ResourceLocation MODIFIER_UUID = ApotheosisModernRagnarok.loc("verdant_ruin_buff");
    public static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, ApotheosisModernRagnarok.loc("verdant_ruin"));

    public VerdantRuinEffect(MobEffectCategory category, Color color) {
        super(category, color.getRGB());
        NeoForge.EVENT_BUS.register(this);
    }

    public static VerdantRuinEffect create() {
        return (VerdantRuinEffect) new VerdantRuinEffect(MobEffectCategory.BENEFICIAL, new Color(45, 141, 137, 255))
                .addAttributeModifier(GunAttributes.HEADSHOT_MULTIPLIER, MODIFIER_UUID, 0.15F, AttributeModifier.Operation.ADD_VALUE);
    }

    @SubscribeEvent
    public final void onGunShoot(GunShootEvent event) {
        var self = holder();
        var shooter = event.getShooter();
        if (shooter == null || shooter.getEffect(self) == null) {
            return;
        }
        if (LootCategory.forItem(event.getGunItemStack()) != ExtraLootCategories.BOLT_ACTION.get()) {
            shooter.removeEffect(self);
        }
    }

    @SubscribeEvent
    public final void onGunshotPost(EntityHurtByGunEvent.Post event) {
        onGunshotPost(event.getAttacker(), event.getBullet(), event.isHeadShot());
    }

    @SubscribeEvent
    public final void onGunshotKill(EntityKillByGunEvent event) {
        onGunshotPost(event.getAttacker(), event.getBullet(), event.isHeadShot());
    }

    @SubscribeEvent
    public final void onGunHitBlock(EntityLeaveLevelEvent event) {
        // 只有实体被 discard 时才进行后续计算
        if (event.getEntity().getRemovalReason() != Entity.RemovalReason.DISCARDED) {
            return;
        }
        var self = holder();
        if (event.getEntity() instanceof Projectile bullet && bullet.getOwner() instanceof LivingEntity shooter) {
            if (shooter.getEffect(self) == null) {
                return;
            }
            if (IGun.mainHandHoldGun(shooter) && !PROCESSED_BULLETS.contains(bullet)) {
                // 打中方块或者射向"浩瀚星辰"时，如果这颗子弹没有爆过头，那么照样视作失误
                onGunshotPost(shooter, bullet, false);
            }
        }
    }

    // MC1.21.1: Replace with data attachment
    private static final Set<Entity> PROCESSED_BULLETS = Collections.newSetFromMap(new WeakHashMap<>());

    public void onGunshotPost(@Nullable LivingEntity shooter, Entity bullet, boolean isHeadshot) {
        if (bullet != null) {
            PROCESSED_BULLETS.add(bullet);
        }
        if (shooter == null || isHeadshot) {
            return;
        }
        var self = holder();
        var instance = shooter.getEffect(self);
        if (instance == null) {
            return;
        }
        // 没有爆头时移除本效果
        shooter.removeEffect(self);
        // 惩罚性伤害
        var level = instance.getAmplifier() + 1;
        if (level < DEFAULT_MAX_LEVEL / 2) {
            return;
        }
        var penalty = shooter.getMaxHealth() * level / DEFAULT_MAX_LEVEL;
        shooter.hurt(shooter.damageSources().source(DAMAGE_TYPE, null, null), penalty);
        shooter.level().playSound(null, shooter, ModContent.Sounds.ARMOR_CRACK.get(), shooter.getSoundSource(), 1, 1);
    }
}
