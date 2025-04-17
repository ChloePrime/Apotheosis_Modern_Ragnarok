package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.entity.EntityKineticBullet;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedKineticBullet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityKineticBullet.class)
public abstract class MixinEntityKineticBullet extends Projectile implements EnhancedKineticBullet {
    private @Unique double amr$waterFrictionFactor = 1;

    @Override
    public void amr$applyWaterFrictionFactor(double factor) {
        amr$waterFrictionFactor *= factor;
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/EntityKineticBullet;isInWater()Z")))
    private Vec3 bulletRiptide(Vec3 instance, double arg, Operation<Vec3> original) {
        if (isInWater()) {
            var oldFriction = 1 - arg;
            var minFriction = friction;
            var newFriction = Mth.lerp(amr$waterFrictionFactor, minFriction, oldFriction);
            return original.call(instance, 1 - newFriction);
        } else {
            return original.call(instance, arg);
        }
    }

    protected MixinEntityKineticBullet(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow(remap = false)
    private float friction;
}
