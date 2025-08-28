package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import mod.chloeprime.apotheosismodernragnarok.common.gunpack.GunApothData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LootController.class, remap = false)
public class MixinLootController {
    @WrapOperation(
            method = "lambda$getAvailableAffixes$0",
            at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apotheosis/affix/Affix;canApplyTo(Lnet/minecraft/world/item/ItemStack;Ldev/shadowsoffire/apotheosis/loot/LootCategory;Ldev/shadowsoffire/apotheosis/loot/LootRarity;)Z"))
    private static boolean blacklist(Affix affix, ItemStack stack, LootCategory category, LootRarity rarity, Operation<Boolean> original) {
        var isInBlacklist = GunApothData.of(stack)
                .filter(apoth -> apoth.getDisabledAffixes().contains(AffixRegistry.INSTANCE.getKey(affix)))
                .isPresent();
        return !isInBlacklist && original.call(affix, stack, category, rarity);
    }
}
