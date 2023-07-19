package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.entity.DamageSourceProjectile;
import com.tac.guns.entity.ProjectileEntity;
import mod.chloeprime.apotheosismodernragnarok.common.internal.LaserProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ProjectileEntity.class, remap = false)
public class MixinProjectileEntity {
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z")
    )
    private boolean disableTickOnLasers_skipPhysics(Level level) {
        return level.isClientSide() || this instanceof LaserProjectile;
    }

    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/entity/ProjectileEntity;setPos(DDD)V")
    )
    private void disableTickOnLasers_doNotSetPos(ProjectileEntity self, double x, double y, double z) {
        if (this instanceof LaserProjectile) {
            return;
        }
        self.setPos(x, y, z);
    }

    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/entity/ProjectileEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )
    private void disableTickOnLasers_doNotSetVelocity(ProjectileEntity self, Vec3 velocity) {
        if (this instanceof LaserProjectile) {
            return;
        }
        self.setDeltaMovement(velocity);
    }

    @Redirect(
            method = "onHit",
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/entity/ProjectileEntity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V")
    )
    private void disableTickOnLasers_doNotDespawnOnHit(ProjectileEntity projectile, Entity.RemovalReason reason) {
        if (reason == Entity.RemovalReason.DISCARDED && this instanceof LaserProjectile) {
            return;
        }
        projectile.remove(reason);
    }

    @Redirect(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/entity/DamageSourceProjectile;setProjectile()Lnet/minecraft/world/damagesource/DamageSource;")
    )
    private DamageSource processDamageSource(DamageSourceProjectile source) {
        source.setProjectile();
        if (this instanceof LaserProjectile laser) {
            laser.processDamageSource(source);
        }
        return source;
    }
}
