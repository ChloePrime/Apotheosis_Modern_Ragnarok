package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import dev.shadowsoffire.apotheosis.ench.enchantments.masterwork.ScavengerEnchant;
import dev.shadowsoffire.apotheosis.spawn.enchantment.CapturingEnchant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Set;

public class GunEnchantmentHooks {
    private static final ItemStack PREDICATE_SWORD = Items.NETHERITE_SWORD.getDefaultInstance();
    private static final Set<Enchantment> BLACKLIST = Set.of(
            Enchantments.SWEEPING_EDGE
    );

    public static boolean isExistingEnchantmentAvailableOnTacMeleeWeapons(Enchantment enchantment) {
        return !BLACKLIST.contains(enchantment)
                && enchantment.category != EnchantmentCategory.BREAKABLE
                && PREDICATE_SWORD.canApplyAtEnchantingTable(enchantment);
    }

    public static boolean isExistingEnchantmentAvailableOnGuns(Enchantment enchantment) {
        return enchantment == Enchantments.MOB_LOOTING
                || enchantment instanceof ScavengerEnchant
                || enchantment instanceof CapturingEnchant;
    }
}
