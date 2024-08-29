package mod.chloeprime.apotheosismodernragnarok.common;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class CommonConfig {
    public static final ForgeConfigSpec.ConfigValue<List<String>> GUN_COMPATIBLE_AFFIX;
    public static final List<String> DEFAULT_GUN_COMPATIBLE_AFFIX = new ArrayList<>(List.of(
            Apotheosis.loc("glacial").toString(),
            Apotheosis.loc("infernal").toString(),
            Apotheosis.loc("intricate").toString(),
            Apotheosis.loc("lacerating").toString(),
            Apotheosis.loc("shredding").toString(),
            Apotheosis.loc("vampiric").toString()
    ));

    static final ForgeConfigSpec SPEC;

    static {
        var builder = new ForgeConfigSpec.Builder();

        GUN_COMPATIBLE_AFFIX = builder.
                comment("List of affix that'll be make compatible with guns")
                .define("gun_compatibe_affix", DEFAULT_GUN_COMPATIBLE_AFFIX);

        SPEC = builder.build();
    }
}
