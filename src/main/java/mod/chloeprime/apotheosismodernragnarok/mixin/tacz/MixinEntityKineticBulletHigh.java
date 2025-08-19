package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.entity.EntityKineticBullet;
import mod.chloeprime.apotheosismodernragnarok.common.internal.DamageInfo;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityKineticBullet.class, remap = false, priority = 990)
public class MixinEntityKineticBulletHigh {
    @WrapOperation(
            method = "tacAttackEntity",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean recordOriginalDamage(Entity victim, DamageSource source, float amount, Operation<Boolean> original) {
        ((DamageInfo) source).amr$setOriginalDamage(amount);
        return original.call(victim, source, amount);
    }
}
