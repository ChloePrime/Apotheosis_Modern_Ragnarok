package mod.chloeprime.apotheosismodernragnarok.common.internal;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.function.Function;
import java.util.function.Predicate;

public interface LootCategoryFactory {
    LootCategory apotheosis_modern_ragnarok$create(String name, Predicate<ItemStack> validator, Function<ItemStack, EquipmentSlot[]> slotGetter);
    void apotheosis_modern_ragnarok$setRanged(boolean isRanged);
}
