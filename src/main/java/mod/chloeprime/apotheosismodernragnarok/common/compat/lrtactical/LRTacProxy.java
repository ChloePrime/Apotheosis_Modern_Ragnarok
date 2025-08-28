package mod.chloeprime.apotheosismodernragnarok.common.compat.lrtactical;

import dev.shadowsoffire.apotheosis.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.Optional;

public class LRTacProxy {
    public static final boolean INSTALLED = ModList.get().isLoaded("lrtactical");

    public static Optional<LootCategory> getLRTacMeleeType(ItemStack stack) {
        return INSTALLED
                ? getLRTacMeleeTypeSafeImpl(stack)
                : Optional.empty();
    }

    private static boolean getLRTacMeleeTypeSafeImplErrorLogged = false;
    private static Optional<LootCategory> getLRTacMeleeTypeSafeImpl(ItemStack stack) {
        try {
            return LRTacProxyImpl.getLRTacMeleeType(stack);
        } catch (IncompatibleClassChangeError error) {
            if (!getLRTacMeleeTypeSafeImplErrorLogged) {
                getLRTacMeleeTypeSafeImplErrorLogged = true;
                ApotheosisModernRagnarok.logError("Error when getting loot category for LRTac melee weapon", error);
            }
            return Optional.empty();
        }
    }
}
