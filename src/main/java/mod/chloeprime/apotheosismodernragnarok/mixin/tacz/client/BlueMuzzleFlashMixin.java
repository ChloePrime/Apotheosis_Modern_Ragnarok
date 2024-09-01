package mod.chloeprime.apotheosismodernragnarok.mixin.tacz.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.functional.MuzzleFlashRender;
import com.tacz.guns.client.resource.pojo.display.gun.MuzzleFlash;
import mod.chloeprime.apotheosismodernragnarok.client.MagicalShotAffixVisuals;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.MagicalShotAffix;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mod.chloeprime.apotheosismodernragnarok.client.MagicalShotAffixVisuals.isMagicGunState;

public class BlueMuzzleFlashMixin {
    @Mixin(value = BedrockGunModel.class, remap = false)
    public static class CaptureState {
        @Inject(method = "render", at = @At("HEAD"))
        public void beginRender(PoseStack matrixStack, ItemStack gunItem, ItemDisplayContext transformType, RenderType renderType, int light, int overlay, CallbackInfo ci) {
            isMagicGunState = MagicalShotAffix.isMagicGun(gunItem);
        }
    }

    @Mixin(value = MuzzleFlashRender.class, remap = false   )
    public static class ModifyTexture {
        @ModifyExpressionValue(
                method = "doRender",
                at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/pojo/display/gun/MuzzleFlash;getTexture()Lnet/minecraft/resources/ResourceLocation;")
        )
        private static ResourceLocation modifyTexture(ResourceLocation original) {
            return isMagicGunState ? MagicalShotAffixVisuals.BLUE_MUZZLE_FLASH : original;
        }

        @Inject(method = "doRender", at = @At("RETURN"))
        private static void cleanup(int light, int overlay, MuzzleFlash muzzleFlash, long time, CallbackInfo ci) {
            isMagicGunState = false;
        }
    }
}
