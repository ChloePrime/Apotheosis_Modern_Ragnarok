package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.common.gunpack.GunApothData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = LootCategory.class, remap = false)
public class MixinLootCategory {
    @Inject(method = "forItem", at = @At("HEAD"), cancellable = true)
    private static void gunpackOverride(ItemStack item, CallbackInfoReturnable<LootCategory> cir) {
        if (item.isEmpty()) {
            return;
        }
        GunApothData.of(item).ifPresent(apoth -> {
            if (apoth.loot_category_override != null) {
                Optional.ofNullable(ResourceLocation.tryParse(apoth.loot_category_override))
                        .map(Apoth.BuiltInRegs.LOOT_CATEGORY::get)
                        .ifPresent(cir::setReturnValue);
            }
        });
    }
}
