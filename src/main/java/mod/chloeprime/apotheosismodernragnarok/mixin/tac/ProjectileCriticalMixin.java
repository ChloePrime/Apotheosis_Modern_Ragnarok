package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.entity.ProjectileEntity;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ProjectileEntity.class, remap = false)
public class ProjectileCriticalMixin {
    @ModifyVariable(method = "tac_attackEntity", at = @At("HEAD"), argsOnly = true)
    private float modifyAttackDamage(float damage, DamageSource source, Entity target, float originalDamage) {
        if (!(source.getEntity() instanceof Player attacker) || attacker.level.isClientSide()) {
            return damage;
        }
        return DamageUtils.runInFixedCritical(attacker, () -> {
            var hit = ForgeHooks.getCriticalHit(attacker, target, false, 1);
            if (hit == null) {
                return damage;
            }
            attacker.crit(target);
            return damage * hit.getDamageModifier();
        });
    }
}
