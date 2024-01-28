package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.common.Gun;
import com.tac.guns.entity.ProjectileEntity;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.GunModifierHelper;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.apotheosis.Apoth;

@Mixin(value = ProjectileEntity.class, remap = false)
public class ProjectileDamageCalcMixin {
    @Redirect(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lcom/tac/guns/item/GunItem;Lcom/tac/guns/common/Gun;FF)V",
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/util/GunModifierHelper;getModifiedProjectileSpeed(Lnet/minecraft/world/item/ItemStack;D)D")
    )
    private double modifyProjectileSpeed(ItemStack weapon, double base, EntityType<? extends Entity> entityType, Level worldIn, LivingEntity shooter, ItemStack par0, GunItem item, Gun modifiedGun, float randP, float randY) {
        var speed = GunModifierHelper.getModifiedProjectileSpeed(weapon, base);
        if (shooter != null) {
            speed *= shooter.getAttributeValue(Apoth.Attributes.ARROW_VELOCITY);
        }
        return speed;
    }

    @ModifyVariable(method = "tac_attackEntity", at = @At("HEAD"), argsOnly = true)
    private float modifyAttackDamage(float damage, DamageSource source, Entity target, float originalDamage) {
        damage = apotheosis_modern_ragnarok$arrowDamage(damage, source, target);
        damage = apotheosis_modern_ragnarok$critical(damage, source, target);
        return damage;
    }

    @Unique
    private static float apotheosis_modern_ragnarok$arrowDamage(float damage, DamageSource source, Entity target) {
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker.level.isClientSide()) {
            return damage;
        }
        return (float) (damage * attacker.getAttributeValue(Apoth.Attributes.ARROW_DAMAGE));
    }

    @Unique
    private static float apotheosis_modern_ragnarok$critical(float damage, DamageSource source, Entity target) {
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
