package mod.chloeprime.apotheosismodernragnarok.common.gem.framework;

import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GemInjectionRegistry extends DynamicRegistry<GemInjection> {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final GemInjectionRegistry INSTANCE = new GemInjectionRegistry();

    public GemInjectionRegistry() {
        super(LOGGER, "amr_gem_injections", true, false);
    }

    @Override
    protected void registerBuiltinCodecs() {
        registerDefaultCodec(ApotheosisModernRagnarok.loc("amr_gem_injection"), GemInjection.CODEC);
    }
}
