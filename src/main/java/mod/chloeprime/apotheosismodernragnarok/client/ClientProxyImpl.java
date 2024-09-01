package mod.chloeprime.apotheosismodernragnarok.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

class ClientProxyImpl {
    @OnlyIn(Dist.CLIENT)
    static boolean hasLang(String langKey) {
        return I18n.exists(langKey);
    }

    @OnlyIn(Dist.CLIENT)
    static Iterable<Entity> getEntitiesFromClientLevel(Level level) {
        return ((ClientLevel) level).entitiesForRendering();
    }

    @OnlyIn(Dist.CLIENT)
    public static void runDeferred(Runnable code) {
        MC.execute(code);
    }

    @OnlyIn(Dist.CLIENT)
    public static final Minecraft MC = Minecraft.getInstance();
}
