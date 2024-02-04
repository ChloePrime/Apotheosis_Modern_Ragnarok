package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.tac.guns.Config;
import com.tac.guns.item.GunItem;
import cpw.mods.util.LambdaExceptionUtils;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.AdsChargeAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.GunDamageAffix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import shadows.apotheosis.Apoth;
import shadows.apotheosis.adventure.affix.AffixHelper;

import java.util.Optional;
import java.util.concurrent.Callable;

public class DamageUtils {
    public static boolean isGunShotFirstPart(DamageSource source) {
        return ((ExtendedDamageSource) source).apotheosis_modern_ragnarok$isGunshotFirstPart();
    }

    public static boolean isGunShot(DamageSource source) {
        return ((ExtendedDamageSource) source).apotheosis_modern_ragnarok$isGunshot();
    }

    public static boolean isHeadshot(DamageSource source) {
        return ((ExtendedDamageSource) source).apotheosis_modern_ragnarok$isHeadshot();
    }

    public static ItemStack getWeapon(DamageSource source) {
        return ((ExtendedDamageSource) source).apotheosis_modern_ragnarok$getWeapon();
    }

    public static float modifyDamage(ItemStack stack, float originalDamage) {
        var damage = originalDamage;
        var affixes = AffixHelper.getAffixes(stack);
        damage = GunDamageAffix.modifyDamage(stack, affixes, damage);
        damage = AdsChargeAffix.modifyDamage(stack, affixes, damage);
        return damage;
    }

    public static void ifIsDamageFirstPartOrElse(DamageSource source, float amount, FloatConsumer action, Runnable elseRun) {
        float realAmount;
        if (Config.COMMON.gameplay.bulletsIgnoreStandardArmor.get()) {
            var apPercent = Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get().floatValue() * getApOnWeapon(source);
            var keepAp = apPercent == 1;
            if (keepAp != source.isBypassArmor()) {
                elseRun.run();
                return;
            }
            realAmount = amount / (keepAp ? apPercent : 1 - apPercent);
        } else {
            realAmount = amount;
        }
        action.accept(realAmount);
    }

    public static void ifIsDamageFirstPart(DamageSource source, float amount, FloatConsumer action) {
        ifIsDamageFirstPartOrElse(source, amount, action, () -> {});
    }

    public static float fixBaseDamage(float damage, DamageSource source, Entity target) {
        damage = apotheosis_modern_ragnarok$arrowDamage(damage, source, target);
        damage = apotheosis_modern_ragnarok$critical(damage, source, target);
        return damage;
    }

    public static <R> R runInFixedCritical(Player attacker, Callable<R> action) {
        var critChance = Optional.ofNullable(attacker.getAttribute(Apoth.Attributes.CRIT_CHANCE));
        var critDamage = Optional.ofNullable(attacker.getAttribute(Apoth.Attributes.CRIT_DAMAGE));
        var hasCritAttributes = critChance.isPresent() && critDamage.isPresent();
        var hasCritBug = hasCritAttributes &&
                Math.abs(critChance.get().getBaseValue() - 1.5) < 1e-6 &&
                Math.abs(critDamage.get().getBaseValue() - 1.0) < 1e-6;

        if (!hasCritBug) {
            return LambdaExceptionUtils.uncheck(action::call);
        }

        double oldBaseCritChance;
        double oldBaseCritDamage;
        oldBaseCritChance = critChance.get().getBaseValue();
        oldBaseCritDamage = critDamage.get().getBaseValue();

        try {
            // 原版神化默认暴击率 50%，默认暴击倍率为 1 倍（不增加伤害）。
            // 此处将默认暴击率改为1，默认暴击倍率改为 1.5 倍
            critChance.get().setBaseValue(oldBaseCritDamage);
            critDamage.get().setBaseValue(oldBaseCritChance);
            return LambdaExceptionUtils.uncheck(action::call);
        } finally {
            critChance.get().setBaseValue(oldBaseCritChance);
            critDamage.get().setBaseValue(oldBaseCritDamage);
        }
    }

    private static float apotheosis_modern_ragnarok$arrowDamage(float damage, DamageSource source, Entity target) {
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker.level.isClientSide()) {
            return damage;
        }
        return (float) (damage * attacker.getAttributeValue(Apoth.Attributes.ARROW_DAMAGE));
    }

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

    private static float getApOnWeapon(DamageSource source) {
        var weapon = getWeapon(source);
        if (!(weapon.getItem() instanceof GunItem gun)) {
            return 1;
        }
        return gun.getModifiedGun(weapon).getProjectile().getGunArmorIgnore();
    }

    private DamageUtils() {}
}
