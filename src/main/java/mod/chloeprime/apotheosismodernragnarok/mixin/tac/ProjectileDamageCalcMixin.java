package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.common.Gun;
import com.tac.guns.entity.DamageSourceProjectile;
import com.tac.guns.entity.ProjectileEntity;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.GunModifierHelper;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDamageSource;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis.CleavingAffixAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.apotheosis.Apoth;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.AffixInstance;
import shadows.apotheosis.adventure.affix.effect.CleavingAffix;
import shadows.apotheosis.util.DamageSourceUtil;

import java.util.List;
import java.util.Optional;

@Mixin(value = ProjectileEntity.class, remap = false)
public abstract class ProjectileDamageCalcMixin {
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
            method = "tac_attackEntity", at = @At("RETURN")
    )
    private void doPostAttackEffects(DamageSource source, Entity target, float damage, CallbackInfo ci) {
        Optional.ofNullable(shooter).ifPresent(shooter -> {
            if (target instanceof LivingEntity victim) {
                EnchantmentHelper.doPostHurtEffects(victim, shooter);
            }
            EnchantmentHelper.doPostDamageEffects(shooter, target);
            var weapon = getWeapon();
            AffixHelper.getAffixes(weapon).forEach((affix, instance) -> {
                if (!(affix instanceof CleavingAffix cleaving)) {
                    return;
                }
                apotheosis_modern_ragnarok$cleave(source, cleaving, instance, shooter, target, damage);
            });
        });
    }

    @Unique
    @SuppressWarnings("DataFlowIssue")
    private void apotheosis_modern_ragnarok$cleave(
            DamageSource originalSource,
            CleavingAffix affix, AffixInstance instance,
            Entity user, Entity originalTtarget, float originalDamage
    ) {
        if (!apotheosis_modern_ragnarok$cleaving && !user.level.isClientSide) {
            apotheosis_modern_ragnarok$cleaving = true;
            var rarity = instance.rarity();
            var level = instance.level();
            var chance = ((CleavingAffixAccessor) affix).invokeGetChance(rarity, level);
            var targets = ((CleavingAffixAccessor) affix).invokeGetTargets(rarity, level);
            if (user.level.random.nextFloat() < chance && user instanceof Player player) {
                List<Entity> nearby = originalTtarget.level.getEntities(originalTtarget, (new AABB(originalTtarget.blockPosition())).inflate(6.0), CleavingAffix.cleavePredicate(user, originalTtarget));

                for (Entity target : nearby) {
                    if (targets > 0) {
                        var source = new DamageSourceProjectile(originalSource.getMsgId(), originalSource.getDirectEntity(), originalSource.getEntity(), DamageUtils.getWeapon(originalSource));
                        ((DamageSourceUtil.DmgSrcCopy) source).copyFrom(originalSource);
                        ((ExtendedDamageSource) source).apotheosis_modern_ragnarok$setGunshot(true);
                        ((ExtendedDamageSource) source).apotheosis_modern_ragnarok$setGunshotFirstPart(true);
                        tac_attackEntity(source, target, originalDamage);
                        --targets;
                    } else {
                        break;
                    }
                }
            }

            apotheosis_modern_ragnarok$cleaving = false;
        }
    }

    @Unique private boolean apotheosis_modern_ragnarok$cleaving;


    @Shadow protected abstract void tac_attackEntity(DamageSource source, Entity entity, float damage);
    @Shadow public abstract ItemStack getWeapon();
    @Shadow protected LivingEntity shooter;
}
