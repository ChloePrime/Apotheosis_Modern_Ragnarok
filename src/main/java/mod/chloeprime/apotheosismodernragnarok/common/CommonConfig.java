package mod.chloeprime.apotheosismodernragnarok.common;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    public static final ForgeConfigSpec.ConfigValue<List<String>> ARMOR_SQUASH_BLACKLIST;
    public static final List<String> DEFAULT_ARMOR_SQUASH_BLACKLIST = Lists.newArrayList(
            "minecraft:player",
            "minecraft:armor_stand",
            "dummmmmmy:target_dummy"
    );

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent event) {
        if (!(event instanceof ModConfigEvent.Loading) && !(event instanceof ModConfigEvent.Reloading)) {
            return;
        }
        asb_dirty = true;
    }

    public static boolean isArmorSquashBlacklist(@Nonnull Entity entity) {
        if (asb_dirty) {
            synchronized (AS_BLACKLIST) {
                if (asb_dirty) {
                    AS_BLACKLIST.clear();
                    var reg = ForgeRegistries.ENTITY_TYPES;
                    ARMOR_SQUASH_BLACKLIST.get().stream()
                            .map(ResourceLocation::new)
                            .filter(reg::containsKey)
                            .map(reg::getValue)
                            .filter(Objects::nonNull)
                            .forEach(AS_BLACKLIST::add);
                }
                asb_dirty = false;
            }
        }
        return AS_BLACKLIST.contains(entity.getType());
    }

    static volatile boolean asb_dirty = true;
    static final Set<EntityType<?>> AS_BLACKLIST = Collections.newSetFromMap(new ConcurrentHashMap<>());
    static final ForgeConfigSpec SPEC;

    static {
        var builder = new ForgeConfigSpec.Builder();

        ARMOR_SQUASH_BLACKLIST = builder.
                comment("Entity types that armor squash will not take effect on")
                .define("armor_squash_blacklist", DEFAULT_ARMOR_SQUASH_BLACKLIST);

        SPEC = builder.build();
    }
}
