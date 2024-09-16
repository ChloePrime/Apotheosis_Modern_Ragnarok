package mod.chloeprime.apotheosismodernragnarok.mixin.tacz.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.event.CameraSetupEvent;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.util.DangerousMixin;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@DangerousMixin
@Mixin(value = CameraSetupEvent.class, remap = false)
public class RecoilAttributeMixin {
    @WrapOperation(
            method = "applyCameraRecoil",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/client/player/LocalPlayer;setXRot(F)V")
    )
    private static void modifyVRot(LocalPlayer player, float v, Operation<Void> original) {
        var delta = player.getXRot() - v;
        delta *= (float) player.getAttributeValue(ModContent.Attributes.V_RECOIL.get());
        original.call(player, player.getXRot() - delta);
    }

    @WrapOperation(
            method = "applyCameraRecoil",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/client/player/LocalPlayer;setYRot(F)V")
    )
    private static void modifyHRot(LocalPlayer player, float v, Operation<Void> original) {
        var delta = player.getYRot() - v;
        delta *= (float) player.getAttributeValue(ModContent.Attributes.H_RECOIL.get());
        original.call(player, player.getYRot() - delta);
    }
}
