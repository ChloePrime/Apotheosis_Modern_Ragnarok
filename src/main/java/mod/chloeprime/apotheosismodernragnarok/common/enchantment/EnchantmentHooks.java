package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import dev.shadowsoffire.apotheosis.ench.enchantments.masterwork.ScavengerEnchant;
import dev.shadowsoffire.apotheosis.spawn.enchantment.CapturingEnchant;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchantmentHooks {
    public static boolean isExistingEnchantmentAvailableOnGuns(Enchantment enchantment) {
        return enchantment == Enchantments.MOB_LOOTING
                || enchantment instanceof ScavengerEnchant
                || enchantment instanceof CapturingEnchant;
    }
}
