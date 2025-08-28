package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.tacz.guns.api.item.IGun;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.GunEnchantmentHooks;
import mod.chloeprime.apotheosismodernragnarok.common.gunpack.GunApothData;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class)
public abstract class MakeGunEnchantableMixin {
    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void gunIsEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IGun) {
            cir.setReturnValue(true);
        }
    }

    @Mixin(IItemExtension.class)
    public interface MixinItemExtensionInterface {
        @Inject(method = "getEnchantmentValue", at = @At("HEAD"), remap = false, cancellable = true)
        private void giveEnchantmentValue(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
            if (!(this instanceof IGun)) {
                return;
            }
            var gun = Gunsmith.getGunInfo(stack).orElse(null);
            if (gun == null) {
                return;
            }
            cir.setReturnValue(GunApothData.of(gun)
                    .map(apd -> apd.enchantment_value)
                    .orElseGet(() -> GunEnchantmentHooks.defaultEnchantValue(gun)));
        }

        @Inject(method = "supportsEnchantment", at = @At("HEAD"), remap = false, cancellable = true)
        private void canApplyAtEnchantingTable(ItemStack stack, Holder<Enchantment> enchantment, CallbackInfoReturnable<Boolean> cir) {
            GunEnchantmentHooks.canGunApplyEnchantmentAtTable(stack, enchantment, cir::setReturnValue);
        }
    }
}
