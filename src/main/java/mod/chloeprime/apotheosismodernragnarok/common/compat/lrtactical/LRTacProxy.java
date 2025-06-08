package mod.chloeprime.apotheosismodernragnarok.common.compat.lrtactical;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class LRTacProxy {
    public static final boolean INSTALLED = ModList.get().isLoaded("lrtactical");

    public static Optional<LootCategory> getLRTacMeleeType(ItemStack stack) {
        return INSTALLED
                ? LRTacProxyImpl.getLRTacMeleeType(stack)
                : Optional.empty();
    }
}
