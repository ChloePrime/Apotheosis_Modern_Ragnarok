package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.entity.EntityKineticBullet;
import mod.chloeprime.apotheosismodernragnarok.common.internal.BloodBulletUser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.DamageInfo;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * @since MC1.21.1
 */
public class BloodBulletMixin {
    @Mixin(Entity.class)
    public static class BypassInvulnerability {
        @ModifyExpressionValue(
                method = "isInvulnerableTo",
                at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;isEntityInvulnerableTo(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Z)Z"))
        private boolean bypassInvulnerabilityIfHasMiniumDamage(boolean original, DamageSource source) {
            var info = (DamageInfo) source;
            var bullet = source.getDirectEntity();
            if (bullet instanceof Projectile && bullet instanceof BloodBulletUser user) {
                info.amr$setDefenseIgnoreRatio(user.amr$getDefenseIgnoreRatio());
            }
            if (info.amr$getOriginalDamage() > 0 && info.amr$getDefenseIgnoreRatio() > 0) {
                return false;
            }
            return original;
        }
    }

    @Mixin(DamageContainer.class)
    public static abstract class ReduceDefenseEffort {

        @ModifyExpressionValue(
                method = "setReduction",
                at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/damagesource/DamageContainer;modifyReduction(Lnet/neoforged/neoforge/common/damagesource/DamageContainer$Reduction;F)F"))
        private float reduceDefenseEffort(float original) {
            return amr$modifyReducedDamage(original);
        }

        @ModifyExpressionValue(
                method = "setBlockedDamage",
                at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/entity/living/LivingShieldBlockEvent;getBlockedDamage()F"))
        private float reduceShieldDefenseEffort(float original) {
            return amr$modifyReducedDamage(original);
        }

        @ModifyVariable(
                method = "setNewDamage", at = @At("HEAD"),
                argsOnly = true, ordinal = 0)
        private float reduceSetNewDamageDefenseEffort(float value) {
            return newDamage - amr$modifyReducedDamage(newDamage - value);
        }

        @Unique
        private float amr$modifyReducedDamage(float original) {
            if (original <= 0) {
                return original;
            }
            var info = (DamageInfo) getSource();
            if (info.amr$getDefenseIgnoreRatio() <= 0) {
                return original;
            }
            return original * Mth.clamp(1 - info.amr$getDefenseIgnoreRatio(), 0, 1);
        }

        @Shadow public abstract DamageSource getSource();
        @Shadow private float newDamage;
    }

    @Mixin(value = EntityKineticBullet.class, remap = false, priority = 990)
    public abstract static class MixinEntityKineticBulletHigh extends Projectile {
        @WrapOperation(
                method = "tacAttackEntity",
                at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
        private boolean recordOriginalDamage(Entity victim, DamageSource source, float amount, Operation<Boolean> original) {
            var info = (DamageInfo) source;
            if (this instanceof BloodBulletUser user) {
                info.amr$setDefenseIgnoreRatio(user.amr$getDefenseIgnoreRatio());
                info.amr$setOriginalDamage(amount);
            }
            return original.call(victim, source, amount);
        }

        public MixinEntityKineticBulletHigh(EntityType<? extends Projectile> entityType, Level level) {
            super(entityType, level);
        }
    }
}
