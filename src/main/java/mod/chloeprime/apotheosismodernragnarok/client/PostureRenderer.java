package mod.chloeprime.apotheosismodernragnarok.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.util.PostureSystem;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client.EntityRenderAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class PostureRenderer {
    public static final ResourceLocation TEXTURE = ApotheosisModernRagnarok.loc("textures/gui/posture_bar.png");

    @SubscribeEvent
    @SuppressWarnings("rawtypes")
    public static void onRenderLiving(RenderLivingEvent.Post event) {
        double posture = PostureSystem.getPosture(event.getEntity());
        if (posture <= 0) {
            return;
        }
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var entity = event.getEntity();
        var pose = event.getPoseStack();
        double distanceSqr = dispatcher.distanceToSqr(entity);
        //noinspection UnstableApiUsage
        if (ForgeHooksClient.isNameplateInRenderDistance(entity, distanceSqr)) {
            pose.pushPose();
            pose.translate(0, entity.getNameTagOffsetY(), 0);
            pose.mulPose(dispatcher.cameraOrientation());
            pose.scale(-0.025F, -0.025F, 0.025F);
            var matrix = pose.last().pose();

            if (((EntityRenderAccessor) event.getRenderer()).invokeShouldShowName(entity)) {
                pose.translate(0, -6, 0);
            }

            RENDER_PLANS.add(new RenderPlan((float) posture, PostureSystem.isPostureBroken(posture), matrix, event.getPackedLight()));

            pose.popPose();
        }
    }

    private record RenderPlan(
            float barFillRate,
            boolean canBeExecuted,
            Matrix4f matrix,
            int packedLight
    ) {
    }

    private static final List<RenderPlan> RENDER_PLANS = new ArrayList<>(256);

    @SubscribeEvent
    public static void onAfterTerrain(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }
        var buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexLightmapColorShader);
        RenderSystem.enableDepthTest();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR);

        var halfLength = 32;
        var halfHeight = 2;
        for (var plan : RENDER_PLANS) {
            // 背景
            var backgroundZ = -1e-3F;
            blit(plan.matrix(), buffer, -halfLength, halfLength, -halfHeight, halfHeight, backgroundZ, 0, 1, 0, 0.25F, plan.packedLight());
            // 条
            var fillRate = plan.barFillRate();
            var foregroundZ = -6e-3F;
            var backgroundMinV = plan.canBeExecuted() ? 0.5F : 0.25F;
            var backgroundMaxV = backgroundMinV + 0.25F;
            blit(plan.matrix(), buffer, -halfLength * fillRate, 0, -halfHeight, halfHeight, foregroundZ, 0, fillRate * 0.5F, backgroundMinV, backgroundMaxV, plan.packedLight());
            blit(plan.matrix(), buffer, 0, halfLength * fillRate, -halfHeight, halfHeight, foregroundZ, 1 - fillRate * 0.5F, 1, backgroundMinV, backgroundMaxV, plan.packedLight());
        }
        BufferUploader.drawWithShader(buffer.end());
        RENDER_PLANS.clear();
    }

    private static void blit(Matrix4f matrix, VertexConsumer builder, float x1, float x2, float y1, float y2, float z, float minU, float maxU, float minV, float maxV, int light) {
        builder.vertex(matrix, x1, y1, z).uv(minU, minV).uv2(light).color(1F, 1, 1, 1).endVertex();
        builder.vertex(matrix, x1, y2, z).uv(minU, maxV).uv2(light).color(1F, 1, 1, 1).endVertex();
        builder.vertex(matrix, x2, y2, z).uv(maxU, maxV).uv2(light).color(1F, 1, 1, 1).endVertex();
        builder.vertex(matrix, x2, y1, z).uv(maxU, minV).uv2(light).color(1F, 1, 1, 1).endVertex();
    }
}
