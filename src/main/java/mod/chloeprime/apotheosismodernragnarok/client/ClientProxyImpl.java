package mod.chloeprime.apotheosismodernragnarok.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

class ClientProxyImpl {
    static boolean hasLang(String langKey) {
        return I18n.exists(langKey);
    }

    static Iterable<Entity> getEntitiesFromClientLevel(Level level) {
        return ((ClientLevel) level).entitiesForRendering();
    }

    public static void runOnMainThread(Runnable code) {
        MC.execute(code);
    }

    public static final Minecraft MC = Minecraft.getInstance();
}
