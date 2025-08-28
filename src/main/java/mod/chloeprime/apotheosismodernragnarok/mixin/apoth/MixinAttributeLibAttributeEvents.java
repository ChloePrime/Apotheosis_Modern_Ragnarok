package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apothic_attributes.impl.AttributeEvents;
import mod.chloeprime.apotheosismodernragnarok.common.eventhandlers.ElementalDamagesOnLeftButtonMeleeWeapons;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttributeEvents.class, remap = false)
public class MixinAttributeLibAttributeEvents {
    @WrapOperation(
            method = "meleeDamageAttributes",
            at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apothic_attributes/util/AttributesUtil;isPhysicalDamage(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean preventDuplicateDamageBonusPerHit(DamageSource src, Operation<Boolean> original) {
        return ElementalDamagesOnLeftButtonMeleeWeapons.isFirstPartOfDuplicateDamage(src) && original.call(src);
    }
}
