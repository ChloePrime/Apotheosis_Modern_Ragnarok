package mod.chloeprime.apotheosismodernragnarok.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.util.PostureSystem;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client.EntityRenderAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(Dist.CLIENT)
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

        if (ClientHooks.isNameplateInRenderDistance(entity, distanceSqr)) {
            Vec3 offset = Optional
                    .ofNullable(entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(event.getPartialTick())))
                    .orElse(Vec3.ZERO);
            pose.pushPose();
            pose.translate(offset.x, offset.y + 0.5, offset.z);
            pose.mulPose(dispatcher.cameraOrientation());
            pose.scale(0.025F, -0.025F, 0.025F);
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
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);

        var halfLength = 32;
        var halfHeight = 2;
        for (var plan : RENDER_PLANS) {
            // 背景
            var backgroundZ = -6e-3F;
            blit(plan.matrix(), buffer, -halfLength, halfLength, -halfHeight, halfHeight, backgroundZ, 0, 1, 0, 0.25F, plan.packedLight());
            // 条
            var fillRate = plan.barFillRate();
            var foregroundZ = -1e-3F;
            var backgroundMinV = plan.canBeExecuted() ? 0.5F : 0.25F;
            var backgroundMaxV = backgroundMinV + 0.25F;
            blit(plan.matrix(), buffer, -halfLength * fillRate, 0, -halfHeight, halfHeight, foregroundZ, 0, fillRate * 0.5F, backgroundMinV, backgroundMaxV, plan.packedLight());
            blit(plan.matrix(), buffer, 0, halfLength * fillRate, -halfHeight, halfHeight, foregroundZ, 1 - fillRate * 0.5F, 1, backgroundMinV, backgroundMaxV, plan.packedLight());
        }
        Optional.ofNullable(buffer.build()).ifPresent(BufferUploader::drawWithShader);
        RENDER_PLANS.clear();
        RenderSystem.disableBlend();
    }

    private static void blit(Matrix4f matrix, VertexConsumer builder, float x1, float x2, float y1, float y2, float z, float minU, float maxU, float minV, float maxV, int light) {
        builder.addVertex(matrix, x1, y1, z).setColor(1F, 1, 1, 1).setUv(minU, minV).setLight(light);
        builder.addVertex(matrix, x1, y2, z).setColor(1F, 1, 1, 1).setUv(minU, maxV).setLight(light);
        builder.addVertex(matrix, x2, y2, z).setColor(1F, 1, 1, 1).setUv(maxU, maxV).setLight(light);
        builder.addVertex(matrix, x2, y1, z).setColor(1F, 1, 1, 1).setUv(maxU, minV).setLight(light);
    }
}
