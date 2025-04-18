package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.tacz.guns.api.item.IGun;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.GunEnchantmentHooks;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGunData;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class, priority = 1001)
public abstract class MakeGunEnchantableMixin implements IForgeItem {

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void gunIsEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof IGun)) {
            return;
        }
        cir.setReturnValue(true);
    }

    @Dynamic
    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), remap = false, cancellable = true)
    private void giveEnchantmentValue(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!(this instanceof IGun)) {
            return;
        }
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
        if (!(this instanceof IGun)) {
            return;
        }
        var gun = Gunsmith.getGunInfo(stack).orElse(null);
        if (gun == null) {
            return;
        }
        // 近战武器的情况
        if (GunPredicate.isMeleeGun(gun.index())) {
            if (enchantment.category == EnchantmentCategory.BREAKABLE) {
                return;
            }
            cir.setReturnValue(GunEnchantmentHooks.isExistingEnchantmentAvailableOnTacMeleeWeapons(enchantment));
        }
        // 枪械
        else {
            if (enchantment.category.canEnchant(stack.getItem())) {
                return;
            }
            var available = GunEnchantmentHooks.isExistingEnchantmentAvailableOnGuns(enchantment) || switch (gun.index().getType()) {
                case "pistol" -> (enchantment.category == ModContent.Enchantments.CAT_PISTOL);
                case "sniper" -> (enchantment.category == ModContent.Enchantments.CAT_SNIPER);
                case "rifle" -> (enchantment.category == ModContent.Enchantments.CAT_RIFLE);
                case "shotgun" -> (enchantment.category == ModContent.Enchantments.CAT_SHOTGUN);
                case "smg" -> (enchantment.category == ModContent.Enchantments.CAT_SMG);
                case "rpg" -> (enchantment.category == ModContent.Enchantments.CAT_HEAVY_WEAPON);
                case "mg" -> (enchantment.category == ModContent.Enchantments.CAT_MACHINE_GUN);
                default -> false;
            };
            cir.setReturnValue(available);
        }
    }
}
