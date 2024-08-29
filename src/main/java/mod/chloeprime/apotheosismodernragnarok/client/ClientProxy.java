package mod.chloeprime.apotheosismodernragnarok.client;

import net.minecraftforge.fml.loading.FMLLoader;

public class ClientProxy {
    public static final boolean DEDICATED_SERVER = FMLLoader.getDist().isDedicatedServer();

    public static boolean hasLang(String langKey) {
        return DEDICATED_SERVER || ClientProxyImpl.hasLang(langKey);
    }
}
