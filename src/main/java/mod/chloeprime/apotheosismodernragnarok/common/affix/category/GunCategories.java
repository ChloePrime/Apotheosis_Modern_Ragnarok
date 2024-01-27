package mod.chloeprime.apotheosismodernragnarok.common.affix.category;

import com.tac.guns.item.GunItem;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 */
public enum GunCategories implements IExtensibleEnum, Predicate<ItemStack> {
    ALL((stack, gun) -> true),
    SEMI_AUTO((stack, item) -> !item.getGun().getGeneral().isAuto()),
    FULL_AUTO((stack, item) -> item.getGun().getGeneral().isAuto()),
    LARGE_CALIBER((stack, item) -> stack.is(Tags.LARGE_CALIBER_WEAPONS));

    @FunctionalInterface
    public interface Predicate extends BiPredicate<ItemStack, GunItem> {}

    public static class Tags {
        public static final TagKey<Item> LARGE_CALIBER_WEAPONS = ItemTags.create(ApotheosisModernRagnarok.loc("guns/large_caliber"));
        public static final TagKey<Item> NERF_CALIBER_BONUS = ItemTags.create(ApotheosisModernRagnarok.loc("affix/clip_expansion/nerf"));
        public static final TagKey<Item> DISABLE_CALIBER_BONUS = ItemTags.create(ApotheosisModernRagnarok.loc("affix/clip_expansion/disable"));
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() instanceof GunItem gi && validator.test(stack, gi);
    }

    public static Stream<GunCategories> filter(Stream<GunCategories> dataset, ItemStack stack) {
        return stack.getItem() instanceof GunItem gi
                ? dataset.filter(cat -> cat.validator.test(stack, gi))
                : Stream.empty();
    }

    public static Stream<GunCategories> match(ItemStack stack) {
        return filter(Arrays.stream(values()), stack);
    }

    GunCategories(Predicate validator) {
        this.validator = validator;
    }

    public static GunCategories create(String name, Predicate predicate) {
        throw new AssertionError("Enum not extended");
    }

    private final Predicate validator;
}
