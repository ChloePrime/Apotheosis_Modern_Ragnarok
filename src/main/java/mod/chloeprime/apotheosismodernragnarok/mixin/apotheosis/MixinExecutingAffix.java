package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunCategories;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadows.apotheosis.adventure.affix.effect.ExecutingAffix;
import shadows.apotheosis.adventure.loot.LootRarity;

import java.util.Map;

@Mixin(value = ExecutingAffix.class, remap = false)
public class MixinExecutingAffix {
    @Inject(method = "canApplyTo", at = @At("HEAD"), cancellable = true)
    private void makeTaggedGunsCanHaveThisAffix(ItemStack stack, LootRarity rarity, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(GunCategories.Tags.ENABLE_EXECUTING)) {
            cir.setReturnValue(this.values.containsKey(rarity));
        }
    }

    @Shadow @Final protected Map<LootRarity, ?> values;
}
