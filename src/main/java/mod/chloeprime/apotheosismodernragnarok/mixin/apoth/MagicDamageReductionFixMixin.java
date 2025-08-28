package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.affix.effect.DamageReductionAffix;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DamageReductionAffix.DamageType.class, remap = false)
public class MagicDamageReductionFixMixin {
    @Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true)
    private static void fixMagicDamageReduction(DamageSource d, CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.FIX_MAGIC_PROTECTION.get()) {
            cir.setReturnValue(d.is(Tags.DamageTypes.IS_MAGIC));
        }
    }
}
