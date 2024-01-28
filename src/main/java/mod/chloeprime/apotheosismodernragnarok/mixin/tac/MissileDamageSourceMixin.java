package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.entity.DamageSourceProjectile;
import com.tac.guns.entity.ProjectileEntity;
import mod.chloeprime.apotheosismodernragnarok.common.internal.MagicProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import javax.annotation.Nullable;

@Mixin(value = ProjectileEntity.class, remap = false)
public class MissileDamageSourceMixin {
    @ModifyArg(
            method = "createExplosion",
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/world/ProjectileExplosion;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)V"),
            index = 2
    )
    private static DamageSource createProperDamageSource(
            Level world, Entity exploder, @Nullable DamageSource oldNullSource, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode
    ) {
        if (!(exploder instanceof ProjectileEntity rocket)) {
            return oldNullSource;
        }
        var ret = new DamageSourceProjectile("bullet", rocket, rocket.getShooter(), rocket.getWeapon());
        if (rocket instanceof MagicProjectile magic) {
            magic.processDamageSource(ret);
        }
        return ret;
    }
}
