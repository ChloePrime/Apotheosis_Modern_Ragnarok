package mod.chloeprime.apotheosismodernragnarok;

import com.mojang.logging.LogUtils;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import javax.annotation.Nullable;

@Mod(ApotheosisModernRagnarok.MOD_ID)
public class ApotheosisModernRagnarok {

    public static final String MOD_ID = "apotheosis_modern_ragnarok";

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @Nullable
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("CallToPrintStackTrace")
    public static void logError(String message, Throwable throwable) {
        if (LOGGER != null) {
            LOGGER.error(message, throwable);
        } else {
            System.out.println("[ApotheosisModernRagnarok/Error] " + message);
            System.out.println("[ApotheosisModernRagnarok/Error] Stacktrace:");
            throwable.printStackTrace();
        }
    }

    public ApotheosisModernRagnarok() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModContent.init0(modEventBus);
        ModContent.init1(ModLoadingContext.get());
        modEventBus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(ModContent::setup);
    }
}
