package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.world.ProjectileExplosion;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(value = ProjectileExplosion.class, remap = false)
public class MissileExplosionDamageMixin {
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
    private float createProperDamageSource(DamageSource source, float damage) {
        try {
            return Optional.ofNullable(apotheosis_modern_ragnarok$capturedTarget)
                    .map(target -> DamageUtils.fixBaseDamage(damage, source, target))
                    .orElse(damage);
        } finally {
            apotheosis_modern_ragnarok$capturedTarget = null;
        }
    }
}
