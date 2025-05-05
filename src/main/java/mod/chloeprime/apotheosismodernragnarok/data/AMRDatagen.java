package mod.chloeprime.apotheosismodernragnarok.data;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class AMRDatagen {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(true, AMRLootProvider.create(generator.getPackOutput()));
        }
    }
}
