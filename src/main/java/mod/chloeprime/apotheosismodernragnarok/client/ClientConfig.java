package mod.chloeprime.apotheosismodernragnarok.client;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLLoader;

public class ClientConfig {
    public static final boolean ENABLED = FMLLoader.getDist().isClient();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DISABLE_EXTRA_PARTICLES;

    static {
        var builder = new ForgeConfigSpec.Builder();

        DISABLE_EXTRA_PARTICLES = builder
                .comment("""
                       If true, prevrent extra particles (magical affix, blood lord gem etc.) from spawning.
                       开启后，关闭额外粒子（诸如魔法武器，嗜血领主宝石等）的生成，有助于缓解挡视野的问题""")
                .define("disable_extra_particles", false);

        SPEC = builder.build();
    }
}
