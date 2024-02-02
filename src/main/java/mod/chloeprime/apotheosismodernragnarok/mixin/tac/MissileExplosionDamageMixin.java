package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.world.ProjectileExplosion;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = ProjectileExplosion.class, remap = false)
public abstract class MissileExplosionDamageMixin extends Explosion {
    @Unique private Entity apotheosis_modern_ragnarok$capturedTarget;

    @ModifyArg(
            method = "explode", remap = true,
            at = @At(value = "INVOKE", remap = true, target = "Lcom/tac/guns/world/ProjectileExplosion;getSeenPercent(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/Entity;)F"),
            index = 1
    )
    private Entity captureHurtTarget(Entity target) {
        apotheosis_modern_ragnarok$capturedTarget = target;
        return target;
    }

    @ModifyArg(
            method = "explode", remap = true,
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 1
    )
    private float fixBaseDamageAmount(DamageSource source, float damage) {
        return Optional.ofNullable(apotheosis_modern_ragnarok$capturedTarget)
                .map(target -> DamageUtils.fixBaseDamage(damage, source, target))
                .orElse(damage);
    }

    @Inject(
            method = "explode", remap = true, at = @At("RETURN")
    )
    private void clearCapturedReference(CallbackInfo ci) {
        apotheosis_modern_ragnarok$capturedTarget = null;
    }

    @Inject(
            method = "explode", remap = true,
            at = @At(value = "INVOKE", remap = true, shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    private void runPostDamageHooks(CallbackInfo ci) {
        var attacker = getDamageSource().getEntity();
        if (attacker == null) {
            return;
        }
        var target = apotheosis_modern_ragnarok$capturedTarget;
        if (target == null) {
            return;
        }
        if (target instanceof LivingEntity victim) {
            EnchantmentHelper.doPostHurtEffects(victim, attacker);
        }
        if (attacker instanceof LivingEntity livingAttacker) {
            EnchantmentHelper.doPostDamageEffects(livingAttacker, target);
        }
    }


    public MissileExplosionDamageMixin(Level pLevel, @Nullable Entity pSource, @Nullable DamageSource pDamageSource, @Nullable ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction) {
        super(pLevel, pSource, pDamageSource, pDamageCalculator, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
    }
}
