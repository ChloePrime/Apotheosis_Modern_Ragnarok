package mod.chloeprime.apotheosismodernragnarok.client;

import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GemInjector;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientGemInjector {
    public static boolean WILL_INJECT = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (!WILL_INJECT) {
            return;
        }
        WILL_INJECT = false;
        GemInjector.doInjections();
    }
}
