package mod.chloeprime.apotheosismodernragnarok.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tacz.guns.client.resource.pojo.display.ammo.AmmoParticle;
import com.tacz.guns.client.resource.serialize.Vector3fSerializer;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.joml.Vector3f;

@EventBusSubscriber(Dist.CLIENT)
public class MagicalShotAffixVisuals {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Vector3f.class, new Vector3fSerializer())
            .create();
    public static final ResourceLocation BLUE_MUZZLE_FLASH = ApotheosisModernRagnarok.loc("textures/blue_muzzle_flash.png");
    public static final AmmoParticle MAGIC_PARTICLE = GSON.fromJson("""
            {
              "name": "electric_spark",
              "delta": [
                0,
                0,
                0
              ],
              "speed": 0.1,
              "life_time": 20,
              "count": 30
            }""", AmmoParticle.class);
    public static final AmmoParticle BLOOD_PARTICLE = GSON.fromJson("""
            {
              "name": "apotheosis_modern_ragnarok:blood",
              "delta": [
                0,
                0,
                0
              ],
              "speed": 0.05,
              "life_time": 50,
              "count": 30
            }""", AmmoParticle.class);

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MAGIC_PARTICLE.setParticleOptions(ParticleTypes.ELECTRIC_SPARK);
            BLOOD_PARTICLE.setParticleOptions(ModContent.Particles.BLOOD.get());
        });
    }

    public static boolean isMagicGunState;
}
