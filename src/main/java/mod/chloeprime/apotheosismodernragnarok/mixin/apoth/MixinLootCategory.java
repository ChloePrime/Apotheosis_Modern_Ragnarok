package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.common.gunpack.GunApothData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = LootCategory.class, remap = false)
public class MixinLootCategory {
    @Inject(method = "forItem", at = @At("HEAD"), cancellable = true)
    private static void gunpackOverride(ItemStack item, CallbackInfoReturnable<LootCategory> cir) {
        if (item.isEmpty()) {
            return;
        }
        GunApothData.of(item).ifPresent(apoth -> {
            if (apoth.loot_category_override != null) {
                var override = BY_ID.get(apoth.loot_category_override);
                if (override != null) {
                    cir.setReturnValue(override);
                }
            }
        });
    }

    @Shadow @Final public static Map<String, LootCategory> BY_ID;
}
