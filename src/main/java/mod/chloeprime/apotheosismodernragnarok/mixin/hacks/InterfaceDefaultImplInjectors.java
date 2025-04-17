package mod.chloeprime.apotheosismodernragnarok.mixin.hacks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
public final class InterfaceDefaultImplInjectors {
    @Mixin(value = net.minecraft.world.item.Item.class, priority = 0)
    public static abstract class Item implements IForgeItem {
        @Override
        @Intrinsic
        public int getEnchantmentValue(ItemStack stack) {
            return IForgeItem.super.getEnchantmentValue(stack);
        }

        @Override
        @Intrinsic
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return IForgeItem.super.canApplyAtEnchantingTable(stack, enchantment);
        }
    }
}
