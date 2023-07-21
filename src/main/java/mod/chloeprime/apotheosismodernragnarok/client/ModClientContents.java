package mod.chloeprime.apotheosismodernragnarok.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ModClientContents {

    @OnlyIn(Dist.CLIENT)
    public static class RenderTypes extends RenderType {
        public static final Function<ResourceLocation, RenderType> MAGIC_LASER = Util.memoize(texture -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(sssMagicLaser)
                    .setTextureState(new TextureStateShard(texture, true, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .createCompositeState(false);
            return RenderType.create(
                    ApotheosisModernRagnarok.loc("magic_laser").toString(),
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    256, true, false, state
            );
        });

        public static final Function<ResourceLocation, RenderType> MAGIC_FIREBALL = Util.memoize(texture -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(sssMagicLaser)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setCullState(NO_CULL)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .createCompositeState(true);
            return RenderType.create(
                    ApotheosisModernRagnarok.loc("magic_fireball").toString(),
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    256, true, false, state
            );
        });

        private RenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        }
    }

    static ShaderInstance magicLaserShader;
    static final RenderStateShard.ShaderStateShard sssMagicLaser = new RenderStateShard.ShaderStateShard(() -> magicLaserShader);

    private ModClientContents() {}
}
