package mod.chloeprime.apotheosismodernragnarok.common.internal;

import com.google.gson.Gson;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.loot.LootRarity;

/**
 * yep, this is used to steal the GSON in class Affix, instead of anything ingame :P
 * @see MemberStealerAffix#getGson  what this class is used for
 */
public class MemberStealerAffix extends Affix {
    public static Gson getGson() {
        return GSON;
    }

    private MemberStealerAffix() {
        super(AffixType.ANCIENT);
        throw new AssertionError();
    }

    public boolean canApplyTo(ItemStack itemStack, LootRarity lootRarity) {
        throw new AssertionError();
    }
}
