package mod.chloeprime.apotheosismodernragnarok.common.internal;

import com.tac.guns.common.Gun;
import com.tac.guns.item.GunItem;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.LootCategoryBuilder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @see mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunCategories
 */
public class LootCategoryExtensions {
    public static final LootCategory GUN = create("GUN", stack -> stack.getItem() instanceof GunItem, EquipmentSlot.MAINHAND).isRanged().create();

    public static LootCategoryBuilder create(String name, Predicate<ItemStack> validator, Function<ItemStack, EquipmentSlot[]> slotGetter) {
        var factory = (LootCategoryFactory) (Object) LootCategory.NONE;
        return new LootCategoryBuilder(factory.apotheosis_modern_ragnarok$create(name, validator, slotGetter));
    }

    public static LootCategoryBuilder create(String name, Predicate<ItemStack> validator, EquipmentSlot slot) {
        var slotArr = new EquipmentSlot[]{slot};
        return  create(name, validator, stack -> slotArr);
    }

    private static Optional<Gun> getGun(ItemStack stack) {
        return stack.getItem() instanceof GunItem gun ? Optional.of(gun.getGun()) : Optional.empty();
    }

    public static void init() {}

    private LootCategoryExtensions() {}
}
