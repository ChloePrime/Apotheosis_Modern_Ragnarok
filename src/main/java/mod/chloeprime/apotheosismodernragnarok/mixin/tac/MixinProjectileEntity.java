package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.entity.DamageSourceProjectile;
import com.tac.guns.entity.ProjectileEntity;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDamageSource;
import mod.chloeprime.apotheosismodernragnarok.common.internal.LaserProjectile;
import mod.chloeprime.apotheosismodernragnarok.common.internal.MagicProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = ProjectileEntity.class, remap = false)
public class MixinProjectileEntity {
    @Redirect(
            method = "tick", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z", remap = true)
    )
    private boolean disableTickOnLasers_skipPhysics(Level level) {
        return level.isClientSide() || this instanceof LaserProjectile;
    }

    @Redirect(
            method = "tick", remap = true,
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/entity/ProjectileEntity;setPos(DDD)V", remap = true)
    )
    private void disableTickOnLasers_doNotSetPos(ProjectileEntity self, double x, double y, double z) {
        if (this instanceof LaserProjectile) {
            return;
        }
        self.setPos(x, y, z);
    }

    @Redirect(
            method = "tick", remap = true,
            at = @At(value = "INVOKE", remap = true, target = "Lcom/tac/guns/entity/ProjectileEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )
    private void disableTickOnLasers_doNotSetVelocity(ProjectileEntity self, Vec3 velocity) {
        if (this instanceof LaserProjectile) {
            return;
        }
        self.setDeltaMovement(velocity);
    }

    @Redirect(
            method = "onHit",
            at = @At(value = "INVOKE", remap = true, target = "Lcom/tac/guns/entity/ProjectileEntity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V")
    )
    private void disableTickOnLasers_doNotDespawnOnHit(ProjectileEntity projectile, Entity.RemovalReason reason) {
        if (reason == Entity.RemovalReason.DISCARDED && this instanceof LaserProjectile) {
            return;
        }
        projectile.remove(reason);
    }

    @Redirect(
            method = "onHitEntity",
            at = @At(value = "INVOKE", remap = true, target = "Lcom/tac/guns/entity/DamageSourceProjectile;setProjectile()Lnet/minecraft/world/damagesource/DamageSource;")
    )
    private DamageSource processDamageSource_directHit(DamageSourceProjectile source, Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec, boolean headshot) {
        var ret = source.setProjectile();
        ((ExtendedDamageSource) source).apotheosis_modern_ragnarok$setHeadshot(headshot);
        if (this instanceof MagicProjectile laser) {
            laser.processDamageSource(ret);
        }
        return ret;
    }

    /**
     * 第一段伤害后设置第一段伤害的 flag 为 false
     */
    @Inject(
            method = "tac_attackEntity",
            at = @At(value = "INVOKE", remap = true,
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    ordinal = 0, shift = At.Shift.AFTER)
    )
    private void markNotFirstHit(DamageSource source, Entity entity, float damage, CallbackInfo ci) {
        ((ExtendedDamageSource)source).apotheosis_modern_ragnarok$setGunshotFirstPart(false);
    }

    @ModifyArg(
            method = "createExplosion",
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/world/ProjectileExplosion;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)V"),
            index = 2
    )
    private static DamageSource processDamageSource_explosion(Level world, Entity exploder, @Nullable DamageSource source, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode) {
        if (!(exploder instanceof MagicProjectile magicProjectile)) {
            return source;
        }
        var shooter = exploder instanceof ProjectileEntity projectile ? projectile.getShooter() : null;
        var actualSource = source != null ? source : DamageSource.explosion(shooter);
        magicProjectile.processDamageSource(actualSource);
        return actualSource;
    }
}
