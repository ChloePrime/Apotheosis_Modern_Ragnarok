package mod.chloeprime.apotheosismodernragnarok.common.compat.lrtactical;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class LRTacProxy {
    public static final boolean INSTALLED = ModList.get().isLoaded("lrtactical");

    public static boolean isLRTacMeleeWeapon(ItemStack stack) {
        return INSTALLED && LRTacProxyImpl.isLRTacMeleeWeapon(stack);
    }
}
