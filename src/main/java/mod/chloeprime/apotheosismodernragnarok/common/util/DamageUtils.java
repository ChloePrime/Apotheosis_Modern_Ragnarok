package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.tac.guns.Config;
import com.tac.guns.entity.DamageSourceProjectile;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.AdsChargeAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.GunDamageAffix;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.AffixHelper;

public class DamageUtils {
    public static float modifyDamage(ItemStack stack, float originalDamage) {
        var damage = originalDamage;
        var affixes = AffixHelper.getAffixes(stack);
        damage = GunDamageAffix.modifyDamage(stack, affixes, damage);
        damage = AdsChargeAffix.modifyDamage(stack, affixes, damage);
        return damage;
    }

    public static void ifIsKeptDamageOrElse(DamageSourceProjectile source, float amount, FloatConsumer action, Runnable elseRun) {
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

    public static void ifIsKeptDamage(DamageSourceProjectile source, float amount, FloatConsumer action) {
        ifIsKeptDamageOrElse(source, amount, action, () -> {});
    }

    private DamageUtils() {}
}
