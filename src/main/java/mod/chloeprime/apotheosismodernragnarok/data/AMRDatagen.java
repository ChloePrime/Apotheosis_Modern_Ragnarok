package mod.chloeprime.apotheosismodernragnarok.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public final class AMRDatagen {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        if (event.includeServer()) {
            event.createDatapackRegistryObjects(new RegistrySetBuilder()
                    .add(Registries.ENCHANTMENT, AMREnchantProvider::bootstrap));
            generator.addProvider(true, AMRLootProvider.create(generator.getPackOutput(), event.getLookupProvider()));
            generator.addProvider(true, new AMREnchantProvider.Tags(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        }
    }
}
