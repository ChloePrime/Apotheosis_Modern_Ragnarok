package mod.chloeprime.apotheosismodernragnarok.common.compat.lrtactical;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import me.xjqsh.lrtactical.api.item.IMeleeWeapon;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

class LRTacProxyImpl {
    public static Optional<LootCategory> getLRTacMeleeType(ItemStack stack) {
        return stack.getItem() instanceof IMeleeWeapon
                ? Optional.of(Apoth.LootCategories.MELEE_WEAPON)
                : Optional.empty();
    }
}
