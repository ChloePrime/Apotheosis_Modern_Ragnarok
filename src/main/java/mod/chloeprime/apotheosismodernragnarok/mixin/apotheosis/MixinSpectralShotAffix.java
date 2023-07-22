package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import com.tac.guns.item.GunItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.apotheosis.adventure.affix.effect.SpectralShotAffix;
import shadows.apotheosis.adventure.loot.LootRarity;

import java.util.function.Consumer;

/**
 * @see mod.chloeprime.apotheosismodernragnarok.common.eventhandlers.SpectralBullets 点亮功能的实现
 */
@Mixin(value = SpectralShotAffix.class, remap = false)
public class MixinSpectralShotAffix {
    @Unique
    private ItemStack apotheosis_modern_ragnarok$current = ItemStack.EMPTY;

    @Inject(method = "addInformation", at = @At("HEAD"))
    private void captureItemStack(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list, CallbackInfo ci) {
        apotheosis_modern_ragnarok$current = stack;
    }

    @Inject(method = "addInformation", at = @At("RETURN"))
    private void releaseItemStack(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list, CallbackInfo ci) {
        apotheosis_modern_ragnarok$current = ItemStack.EMPTY;
    }

    @ModifyArg(
            method = "addInformation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TranslatableComponent;<init>(Ljava/lang/String;[Ljava/lang/Object;)V")
    )
    private String modifyTooltipOnGuns(String pKey) {
        return apotheosis_modern_ragnarok$current.getItem() instanceof GunItem
                ? "affix.apotheosis_modern_ragnarok.spectral.gun.desc"
                : pKey;
    }
}
