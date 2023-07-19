package mod.chloeprime.apotheosismodernragnarok.client;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.client.model.MagicLaserModel;
import mod.chloeprime.apotheosismodernragnarok.client.model.MagicLaserRenderer;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderEventHandler {

    @SubscribeEvent
    public static void renderLayers(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(MagicLaserModel.LAYER_LOCATION, MagicLaserModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModContent.Entities.MAGIC_LASER.get(), MagicLaserRenderer::new);
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent e) throws IOException {
        e.registerShader(new ShaderInstance(
                e.getResourceManager(),
                ApotheosisModernRagnarok.loc("magic_laser"),
                DefaultVertexFormat.NEW_ENTITY
        ), s -> ModClientContents.magicLaserShader = s);
    }
}
