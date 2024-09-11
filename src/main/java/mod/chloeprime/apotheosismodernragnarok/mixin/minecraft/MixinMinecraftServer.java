package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import mod.chloeprime.apotheosismodernragnarok.common.gem.GemInjector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.ReloadableResources.class)
public class MixinMinecraftServer {
    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void injectGemsOnReloadComplete(CloseableResourceManager resourceManager, ReloadableServerResources managers, CallbackInfo ci) {
        GemInjector.doInjections();
    }
}
