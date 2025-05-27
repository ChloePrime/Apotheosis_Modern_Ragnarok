package mod.chloeprime.apotheosismodernragnarok.common.compat.lrtactical;

import me.xjqsh.lrtactical.api.item.IMeleeWeapon;
import net.minecraft.world.item.ItemStack;

class LRTacProxyImpl {
    public static boolean isLRTacMeleeWeapon(ItemStack stack) {
        return stack.getItem() instanceof IMeleeWeapon;
    }
}
