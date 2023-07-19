package mod.chloeprime.apotheosismodernragnarok.common.util;

import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunCategories;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashSet;

public class GunCategorySet extends LinkedHashSet<GunCategories> {
    public boolean contains(ItemStack stack) {
        return GunCategories.filter(stream(), stack).findAny().isPresent();
    }
}
