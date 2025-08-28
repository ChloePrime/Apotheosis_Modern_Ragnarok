package mod.chloeprime.apotheosismodernragnarok.client;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLLoader;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class ClientProxy {
    public static final boolean DEDICATED_SERVER = FMLLoader.getDist().isDedicatedServer();

    public static void runDeferred(Level level, Runnable code) {
        ForkJoinPool.commonPool().execute(() -> runOnMainThread(level, code));
    }

    public static void runOnMainThread(Level level, Runnable code) {
        if (DEDICATED_SERVER || !level.isClientSide) {
            Objects.requireNonNull(level.getServer()).execute(code);
        } else {
            ClientProxyImpl.runOnMainThread(code);
        }
    }

    public static Iterable<Entity> getEntities(Level level) {
        return level.isClientSide
                ? ClientProxyImpl.getEntitiesFromClientLevel(level)
                : ((ServerLevel) level).getEntities().getAll();
    }

    public static boolean hasLang(String langKey) {
        return DEDICATED_SERVER || ClientProxyImpl.hasLang(langKey);
    }
}
