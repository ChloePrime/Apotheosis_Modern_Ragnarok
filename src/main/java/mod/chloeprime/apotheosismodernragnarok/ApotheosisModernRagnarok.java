package mod.chloeprime.apotheosismodernragnarok;

import com.mojang.logging.LogUtils;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.util.debug.DamageAmountDebug;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(ApotheosisModernRagnarok.MOD_ID)
public class ApotheosisModernRagnarok {
    public static final String MOD_ID = "apotheosis_modern_ragnarok";

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings({"CallToPrintStackTrace", "ConstantValue"})
    public static void logError(String message, Throwable throwable) {
        if (LOGGER != null) {
            LOGGER.error(message, throwable);
        } else {
            System.out.println("[ApotheosisModernRagnarok/Error] " + message);
            System.out.println("[ApotheosisModernRagnarok/Error] Stacktrace:");
            throwable.printStackTrace();
        }
    }

    public ApotheosisModernRagnarok(IEventBus modEventBus, ModContainer container) {
        ModContent.init0(modEventBus);
        ModContent.init1(container);
        modEventBus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(ModContent::setup);
        e.enqueueWork(() -> {
            if (!FMLLoader.isProduction()) {
                NeoForge.EVENT_BUS.register(new DamageAmountDebug());
            }
        });
    }
}
