package mod.chloeprime.apotheosismodernragnarok.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.client.ClientProxy;
import mod.chloeprime.apotheosismodernragnarok.client.ModClientContents;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicLaser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.PreRenderListener;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicLaserRenderer<T extends MagicLaser> extends EntityRenderer<T> implements PreRenderListener<T> {
    public static final ResourceLocation TEXTURE_LOCATION = ApotheosisModernRagnarok.loc("textures/entity/magic_laser.png");
    private static final double NANO_TO_SECOND = 1e-9;

    private final MagicLaserModel<T> model;

    public MagicLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new MagicLaserModel<>(context.bakeLayer(MagicLaserModel.LAYER_LOCATION), ModClientContents.RenderTypes.magicLaser);
    }

    @Override
    public boolean preRender(T laser, float partialTicks) {
        ClientProxy.stickLaserToMuzzle(laser, partialTicks);
        return true;
    }

    @Override
    public void render(T entity, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource buffer, int pPackedLight) {
        super.render(entity, pEntityYaw, pPartialTick, poseStack, buffer, pPackedLight);
        poseStack.pushPose();

        var xRot = entity.getViewXRot(pPartialTick);
        var yRot = entity.getViewYRot(pPartialTick);
        var zRot = entity.getRoll();

        poseStack.mulPose(Vector3f.YP.rotationDegrees(-yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        model.setupAnim(zRot);

        var lifetime = (System.nanoTime() - entity.getLocalSpawnTime()) * NANO_TO_SECOND;
        var scale = (float) Math.pow(4, -20 * lifetime);
        var alpha = (float) Math.pow(4, -15 * lifetime);
        var length = entity.getLength();
        poseStack.scale(scale, scale, length);
        poseStack.translate(0, -1.5, 0);

        VertexConsumer consumer = buffer.getBuffer(model.renderType(getTextureLocation(entity)));
        model.renderToBuffer(poseStack, consumer, pPackedLight, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, 1);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return TEXTURE_LOCATION;
    }
}
