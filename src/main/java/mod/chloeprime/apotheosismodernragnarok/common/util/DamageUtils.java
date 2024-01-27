package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.tac.guns.Config;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.AdsChargeAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.GunDamageAffix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.AffixHelper;

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
            var apPercent = Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get().floatValue();
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

    private DamageUtils() {}
}
