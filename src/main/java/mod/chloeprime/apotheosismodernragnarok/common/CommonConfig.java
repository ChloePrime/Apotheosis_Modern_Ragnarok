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
    public static final ForgeConfigSpec.BooleanValue BOLT_ACTION_SHOTGUN_IS_BOLT_ACTION;
    public static final List<String> DEFAULT_ARMOR_SQUASH_BLACKLIST = Lists.newArrayList(
            "minecraft:player",
            "minecraft:armor_stand",
            "dummmmmmy:target_dummy"
    );
    public static final ForgeConfigSpec.IntValue PROJECTION_MAGIC_MAX_FILL_SPEED;
    public static final ForgeConfigSpec.BooleanValue FIX_MAGIC_PROTECTION;

    public static final ForgeConfigSpec.BooleanValue PERFECT_BLOCK_ENABLE_INSTANT_KILL;
    public static final ForgeConfigSpec.DoubleValue POSTURE_BREAK_RANGED_DAMAGE_BONUS;
    public static final ForgeConfigSpec.IntValue MAX_POSTURE_FOR_MINIONS;
    public static final ForgeConfigSpec.IntValue MAX_POSTURE_FOR_BOSSES;

    public static final ForgeConfigSpec.BooleanValue INJECT_ARCHEOLOGY_LOOT_TABLES;
    public static final ForgeConfigSpec.BooleanValue INJECT_CHEST_LOOT_TABLES;

    public static final ForgeConfigSpec.IntValue LRTAC_HEAVY_WEAPON_COOLDOWN_THRESHOLD;

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

        BOLT_ACTION_SHOTGUN_IS_BOLT_ACTION = builder
                .comment("If true, bolt action shotgun is bolt action, otherwise is shotgun")
                .define("ba_shotgun_is_ba", false);

        FIX_MAGIC_PROTECTION = builder
                .comment("If true, Magic damage reduction affix will only work on #forge:is_magic damage types, instead of every armor-piercing damage types")
                .define("fix_magic_protection", true);

        PROJECTION_MAGIC_MAX_FILL_SPEED = builder
                .comment("Max fill amount of projection magic enchantment, per 2 ticks")
                .defineInRange("projection_magic_max_fill_speed", 4, 1, Integer.MAX_VALUE);

        builder.push("perfect_blocking");
        {
            PERFECT_BLOCK_ENABLE_INSTANT_KILL = builder
                    .comment("If true, Executing posture broken enemies will instantly kill it")
                    .define("enable_instant_kill", false);
            POSTURE_BREAK_RANGED_DAMAGE_BONUS = builder
                    .comment("Damage bonus for ranged damage towards posture-broken enemies")
                    .defineInRange("ranged_damage_bonus", 1.5, 0, Float.MAX_VALUE);
            MAX_POSTURE_FOR_MINIONS = builder
                    .comment("Max perfect blocks needed to break minions' posture")
                    .defineInRange("max_posture_for_minions", 1, 1, Integer.MAX_VALUE);
            MAX_POSTURE_FOR_BOSSES = builder
                    .comment("Max perfect blocks needed to break bosses' (#forge:bosses) posture")
                    .defineInRange("max_posture_for_bosses", 5, 1, Integer.MAX_VALUE);
        }
        builder.pop();

        builder.push("loot");
        {
            INJECT_ARCHEOLOGY_LOOT_TABLES = builder
                    .comment("If true, add guns to some archeology loot tables")
                    .define("inject_archeology_loot_tables", true);
            INJECT_CHEST_LOOT_TABLES = builder
                    .comment("If true, add guns to some chest loot tables")
                    .define("inject_chest_loot_tables", true);
        }
        builder.pop();

        builder.push("lesraisins_tactical_equipements");
        {
            LRTAC_HEAVY_WEAPON_COOLDOWN_THRESHOLD = builder
                    .comment("The minimum left attack cooldown for a melee weapon to be considered as Apotheosis heavy weapon.")
                    .defineInRange("lrtac_heavy_weapon_cooldown_threshold", 15, 0, Integer.MAX_VALUE);
        }
        builder.pop();

        SPEC = builder.build();
    }
}
