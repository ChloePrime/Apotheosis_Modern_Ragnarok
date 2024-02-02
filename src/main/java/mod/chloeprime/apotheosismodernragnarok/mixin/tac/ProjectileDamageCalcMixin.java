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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
        return DamageUtils.fixBaseDamage(damage, source, target);
    }

    @Inject(
            method = "tac_attackEntity",
            at = @At("RETURN")
    )
    private void doPostAttackEffects(DamageSource source, Entity target, float damage, CallbackInfo ci) {
        if (target instanceof LivingEntity victim) {
            EnchantmentHelper.doPostHurtEffects(victim, shooter);
        }
        EnchantmentHelper.doPostDamageEffects(shooter, target);
    }

    @Shadow protected LivingEntity shooter;
}
