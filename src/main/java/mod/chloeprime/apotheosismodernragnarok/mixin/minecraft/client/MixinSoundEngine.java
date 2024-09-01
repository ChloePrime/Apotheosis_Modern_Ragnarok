package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client;

import mod.chloeprime.apotheosismodernragnarok.client.ClientCoremodHooks;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void beginPlay(SoundInstance instance, CallbackInfo ci) {
        ClientCoremodHooks.adjustGunSound(instance, ci::cancel);
    }
}
