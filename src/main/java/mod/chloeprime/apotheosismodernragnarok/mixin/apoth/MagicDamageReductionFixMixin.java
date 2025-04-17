package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.adventure.affix.effect.DamageReductionAffix;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DamageReductionAffix.DamageType.class, remap = false)
public class MagicDamageReductionFixMixin {
    private static final @Unique TagKey<DamageType> IS_MAGIC = TagKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation("forge", "is_magic"));

    @Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true)
    private static void fixMagicDamageReduction(DamageSource d, CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.FIX_MAGIC_PROTECTION.get()) {
            cir.setReturnValue(d.is(IS_MAGIC));
        }
    }
}
