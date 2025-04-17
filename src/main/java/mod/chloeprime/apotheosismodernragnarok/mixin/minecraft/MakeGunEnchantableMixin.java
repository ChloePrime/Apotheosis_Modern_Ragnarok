package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.item.IGun;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.AttributedGunEnchantmentBase;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.GunEnchantmentHooks;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGunData;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
            cir.setReturnValue(GunEnchantmentHooks.isExistingEnchantmentAvailableOnGuns(enchantment));
        }
    }

    @Dynamic
    @ModifyReturnValue(method = "getAttributeModifiers", at = @At("RETURN"), remap = false)
    private Multimap<Attribute, AttributeModifier> enchToAttribute(Multimap<Attribute, AttributeModifier> original, EquipmentSlot slot, ItemStack stack) {
        if (!(this instanceof IGun)) {
            return original;
        }
        var ret = original;
        for (var entry : stack.getAllEnchantments().entrySet()) {
            if (entry.getKey() instanceof AttributedGunEnchantmentBase enchantment) {
                var table = ret = original instanceof ImmutableMultimap<Attribute, AttributeModifier>
                        ? LinkedHashMultimap.create()
                        : original;
                enchantment.addAttributes(entry.getValue(), table, slot);
            }
        }
        return ret;
    }
}
