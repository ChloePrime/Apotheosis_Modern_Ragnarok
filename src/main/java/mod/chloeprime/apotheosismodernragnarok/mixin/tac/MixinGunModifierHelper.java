package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.common.Gun;
import com.tac.guns.util.GunModifierHelper;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.GunDamageAffix;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GunModifierHelper.class, remap = false)
public class MixinGunModifierHelper {
    @Inject(method = "getModifiedProjectileDamage", at = @At("RETURN"), cancellable = true)
    private static void inject_applyAffixDamage(ItemStack weapon, float damage, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(GunDamageAffix.modifyDamage(weapon, cir.getReturnValue()));
    }

    @Inject(method = "getModifiedDamage", at = @At("RETURN"), cancellable = true)
    private static void inject_applyAffixDamage(ItemStack weapon, Gun modifiedGun, float damage, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(GunDamageAffix.modifyDamage(weapon, cir.getReturnValue()));
    }
}
