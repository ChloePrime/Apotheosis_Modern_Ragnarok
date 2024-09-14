package mod.chloeprime.apotheosismodernragnarok.common.gem.framework;

import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.client.ClientGemInjector;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static mod.chloeprime.apotheosismodernragnarok.client.ClientProxy.DEDICATED_SERVER;

public class GemInjectionRegistry extends DynamicRegistry<GemInjection> {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final GemInjectionRegistry INSTANCE = new GemInjectionRegistry();

    public GemInjectionRegistry() {
        super(LOGGER, "amr_gem_injections", true, false);
    }

    @Override
    protected void onReload() {
        super.onReload();
        var isClient = !DEDICATED_SERVER && Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER;
        if (isClient) {
            ClientGemInjector.WILL_INJECT = true;
        }
    }

    @Override
    protected void registerBuiltinCodecs() {
        registerDefaultCodec(ApotheosisModernRagnarok.loc("amr_gem_injection"), GemInjection.CODEC);
    }
}
