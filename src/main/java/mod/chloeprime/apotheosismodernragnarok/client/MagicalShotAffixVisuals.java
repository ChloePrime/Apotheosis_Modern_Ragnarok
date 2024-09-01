package mod.chloeprime.apotheosismodernragnarok.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tacz.guns.client.resource.pojo.display.ammo.AmmoParticle;
import com.tacz.guns.client.resource.serialize.Vector3fSerializer;
import com.tacz.guns.entity.EntityKineticBullet;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.MagicalShotAffix;
import mod.chloeprime.apotheosismodernragnarok.mixin.tacz.client.AmmoParticleSpawnerAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber(Dist.CLIENT)
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
              "speed": 0.05,
              "life_time": 20,
              "count": 10
            }""", AmmoParticle.class);

    static {
        MAGIC_PARTICLE.setParticleOptions(ParticleTypes.ELECTRIC_SPARK);
    }

    @SubscribeEvent
    public static void bulletTick(TickEvent.LevelTickEvent event) {
        if (!event.level.isClientSide || event.phase == TickEvent.Phase.END) {
            return;
        }
        var level = (ClientLevel) event.level;
        StreamSupport.stream(level.entitiesForRendering().spliterator(), false)
                .filter(EntityKineticBullet.class::isInstance)
                .map(e -> (EntityKineticBullet)e)
                .filter(MagicalShotAffix::clientIsMagicBullet)
                .forEach(ekb -> AmmoParticleSpawnerAccessor.invokeSpawnParticle(ekb, MAGIC_PARTICLE));
    }

    public static boolean isMagicGunState;
}
