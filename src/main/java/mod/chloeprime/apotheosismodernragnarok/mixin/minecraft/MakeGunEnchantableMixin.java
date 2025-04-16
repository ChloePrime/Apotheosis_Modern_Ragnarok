package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.tacz.guns.api.item.IGun;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.EnchantmentHooks;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGunData;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class, priority = 1001)
public abstract class MakeGunEnchantableMixin implements IForgeItem {
    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void gunIsEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (IGun.getIGunOrNull(stack) != null) {
            cir.setReturnValue(true);
        }
    }

    @Dynamic
    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), remap = false, cancellable = true)
    private void giveEnchantmentValue(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        var gun = Gunsmith.getGunInfo(stack).orElse(null);
        if (gun == null) {
            return;
        }
        cir.setReturnValue(((EnhancedGunData) gun.index().getGunData()).amr$getApothData()
                .map(apd -> apd.enchantment_value)
                .orElse(ArmorMaterials.IRON.getEnchantmentValue()));
    }

    @Dynamic
    @Inject(method = "canApplyAtEnchantingTable", at = @At("HEAD"), remap = false, cancellable = true)
    private void canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment, CallbackInfoReturnable<Boolean> cir) {
        var gun = Gunsmith.getGunInfo(stack).orElse(null);
        if (gun == null) {
            return;
        }
        cir.setReturnValue(EnchantmentHooks.isExistingEnchantmentAvailableOnGuns(enchantment));
    }
}
