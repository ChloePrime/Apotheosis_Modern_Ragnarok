package mod.chloeprime.apotheosismodernragnarok.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.chloeprime.apotheosismodernragnarok.client.ModClientContents;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicFireball;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MagicFireballRenderer extends GeoProjectilesRenderer<MagicFireball> {
    public MagicFireballRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicFireballModel());
    }

    @Override
    public RenderType getRenderType(MagicFireball fireball, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return ModClientContents.RenderTypes.MAGIC_FIREBALL.apply(texture);
    }
}
