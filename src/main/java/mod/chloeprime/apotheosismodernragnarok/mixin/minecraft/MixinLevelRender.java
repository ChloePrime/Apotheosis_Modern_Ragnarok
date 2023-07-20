package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.apotheosismodernragnarok.common.internal.PreRenderListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRender {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void inject_preRender(Entity entity, double pCamX, double pCamY, double pCamZ, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, CallbackInfo ci) {
        if (entityRenderDispatcher.getRenderer(entity) instanceof PreRenderListener listener) {
            if (listener.preRender(entity, pPartialTick)) {
                entity.xOld = entity.getX();
                entity.yOld = entity.getY();
                entity.zOld = entity.getZ();
                entity.yRotO = entity.getYRot();
            }
        }
    }

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;
}
