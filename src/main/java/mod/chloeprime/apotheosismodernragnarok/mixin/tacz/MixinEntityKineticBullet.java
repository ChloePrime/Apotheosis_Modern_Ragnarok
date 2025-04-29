package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.entity.EntityKineticBullet;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedKineticBullet;
import mod.chloeprime.apotheosismodernragnarok.common.util.LeftButtonMeleeFix;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityKineticBullet.class)
public abstract class MixinEntityKineticBullet extends Projectile implements EnhancedKineticBullet {
    // 提高左键近战兼容的事件优先级

    @WrapOperation(
            method = "onHitEntity", remap = false,
            at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"),
            slice = @Slice(
                    from = @At(value = "INVOKE", remap = false, target = "Lcom/tacz/guns/api/event/common/EntityHurtByGunEvent$Pre;<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;FLorg/apache/commons/lang3/tuple/Pair;ZFLnet/minecraftforge/fml/LogicalSide;)V"),
                    to = @At(value = "INVOKE", remap = false, target = "Lcom/tacz/guns/entity/EntityKineticBullet;tacAttackEntity(Lcom/tacz/guns/entity/EntityKineticBullet$MaybeMultipartEntity;FLorg/apache/commons/lang3/tuple/Pair;)V")
            ))
    private boolean fixDamageSources(IEventBus bus, Event event, Operation<Boolean> original) {
        if (event instanceof EntityHurtByGunEvent.Pre pre) {
            LeftButtonMeleeFix.fixDamageTypesOnGunshotPre(pre);
        } else {
            ApotheosisModernRagnarok.logError("Wrong injection point!", new ClassCastException(event.getClass().getCanonicalName()));
        }
        return original.call(bus, event);
    }

    // 激流勇进

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
