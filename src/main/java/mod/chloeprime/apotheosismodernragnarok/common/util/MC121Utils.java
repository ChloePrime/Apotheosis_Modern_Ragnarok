package mod.chloeprime.apotheosismodernragnarok.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class MC121Utils {

    /**
     * 计算附魔影响后的某个值
     */
    public static float evaluateEnchantValue(
            DataComponentType<EnchantmentValueEffect> component,
            ItemStack stack, RandomSource random, float originalValue
    ) {
        var value = new MutableFloat(originalValue);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyUnfilteredValue(component, random, level, value));
        return value.floatValue();
    }

    // 耐久类附魔判断

    private static final Map<Holder<Enchantment>, Boolean> DURABILITY_CACHE = new ConcurrentHashMap<>();

    public static boolean isUnbreakingEnchantment(Holder<Enchantment> enchantment) {
        return DURABILITY_CACHE.computeIfAbsent(enchantment, MC121Utils::isUnbreakingEnchantment0);
    }

    private static boolean isUnbreakingEnchantment0(Holder<Enchantment> enchantment) {
        Holder<?>[] allDurabilityEnchantable = BuiltInRegistries.ITEM.getTag(ItemTags.DURABILITY_ENCHANTABLE)
                .map(set -> set.stream().toArray(Holder[]::new))
                .orElse(new Holder[0]);
        Holder<?>[] allSupported = enchantment.value().definition().supportedItems()
                .stream()
                .toArray(Holder[]::new);
        return Arrays.deepEquals(allDurabilityEnchantable, allSupported);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private static void onDataReload(AddReloadListenerEvent event) {
        DURABILITY_CACHE.clear();
    }
}
