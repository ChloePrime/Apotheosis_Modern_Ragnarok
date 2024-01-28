package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.tac.guns.Config;
import com.tac.guns.item.GunItem;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.AdsChargeAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.GunDamageAffix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public static <R> R runInFixedCritical(Player attacker, Callable<R> action) {
        var critChance = Optional.ofNullable(attacker.getAttribute(Apoth.Attributes.CRIT_CHANCE));
        var critDamage = Optional.ofNullable(attacker.getAttribute(Apoth.Attributes.CRIT_DAMAGE));
        var hasCritAttributes = critChance.isPresent() && critDamage.isPresent();

        double oldBaseCritChance;
        double oldBaseCritDamage;
        if (hasCritAttributes) {
            oldBaseCritChance = critChance.get().getBaseValue();
            oldBaseCritDamage = critDamage.get().getBaseValue();
        } else {
            oldBaseCritChance = 1.5;
            oldBaseCritDamage = 1.0;
        }

        try {
            // 原版神化默认暴击率 50%，默认暴击倍率为 1 倍（不增加伤害）。
            // 此处将默认暴击率改为1，默认暴击倍率改为 1.5 倍
            if (hasCritAttributes) {
                critChance.get().setBaseValue(oldBaseCritDamage);
                critDamage.get().setBaseValue(oldBaseCritChance);
            }
            return action.call();
        } catch (RuntimeException | Error err) {
            throw err;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (hasCritAttributes) {
                critChance.get().setBaseValue(oldBaseCritChance);
                critDamage.get().setBaseValue(oldBaseCritDamage);
            }
        }
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
