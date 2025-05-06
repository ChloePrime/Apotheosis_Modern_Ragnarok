package mod.chloeprime.apotheosismodernragnarok.common.loot;

import com.google.common.collect.Lists;
import com.tacz.guns.init.ModItems;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.LootPoolAccessor;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.LootTableAccessor;
import mod.chloeprime.gunsmithlib.api.common.GunLootFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DefaultLootInjections {
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        var id = event.getName();
        var table = event.getTable();
        switch (id.toString()) {
            // 考古
            case "minecraft:archaeology/trail_ruins_rare":
                if (CommonConfig.INJECT_ARCHEOLOGY_LOOT_TABLES.get()) {
                    injectArcheology(table, new ResourceLocation("tacz", "springfield1873"));
                    injectArcheology(table, new ResourceLocation("tacz", "m1911"));
                    injectArcheology(table, new ResourceLocation("tacz", "db_short"));
                    injectArcheology(table, new ResourceLocation("tacz", "db_long"));
                    // 硝烟革命兼容: 手枪
                    injectArcheology(table, new ResourceLocation("hamster", "webley"));
                    injectArcheology(table, new ResourceLocation("hamster", "m1879revolver"));
                    injectArcheology(table, new ResourceLocation("hamster", "coltm1851"));
                    injectArcheology(table, new ResourceLocation("hamster", "sw_mk2"));
                    injectArcheology(table, new ResourceLocation("hamster", "colt1873"));
                    // 硝烟革命兼容: 狙击枪
                    injectArcheology(table, new ResourceLocation("hamster", "gew98"));
                    injectArcheology(table, new ResourceLocation("hamster", "smle_mk3"));
                    injectArcheology(table, new ResourceLocation("hamster", "mosin9130"));
                    injectArcheology(table, new ResourceLocation("hamster", "win1894"));
                    injectArcheology(table, new ResourceLocation("hamster", "type99"));
                    injectArcheology(table, new ResourceLocation("hamster", "gras1874"));
                    injectArcheology(table, new ResourceLocation("hamster", "m1903"));
                    injectArcheology(table, new ResourceLocation("hamster", "sharps"));
                    injectArcheology(table, new ResourceLocation("hamster", "martinihenry"));
                    injectArcheology(table, new ResourceLocation("hamster", "lebel1886"));
                    injectArcheology(table, new ResourceLocation("hamster", "berthier"));
                    // 硝烟革命兼容: 霰弹枪
                    injectArcheology(table, new ResourceLocation("hamster", "m1887"));
                    injectArcheology(table, new ResourceLocation("hamster", "one_barrel"));
                }
                break;
            case "minecraft:archaeology/desert_pyramid":
            case "minecraft:archaeology/desert_well":
                if (CommonConfig.INJECT_ARCHEOLOGY_LOOT_TABLES.get()) {
                    injectArcheology(table, new ResourceLocation("hamster", "mosin9130"));
                    injectArcheology(table, new ResourceLocation("hamster", "sks"));
                    injectArcheology(table, new ResourceLocation("hamster", "one_barrel"));
                }
                break;
            // 箱子
            case "minecraft:chests/ruined_portal":
            case "minecraft:chests/spawn_bonus_chest":
            case "apotheosis:chests/tome_tower":
                if (CommonConfig.INJECT_CHEST_LOOT_TABLES.get()) {
                    injectChest(table, "injects/chest/for_newcomers");
                }
            case "minecraft:chests/abandoned_mineshaft":
            case "minecraft:chests/igloo_chest":
            case "minecraft:chests/pillager_outpost":
            case "minecraft:chests/shipwreck_supply":
            case "minecraft:chests/simple_dungeon":
            case "minecraft:chests/stronghold_corridor":
            case "minecraft:chests/stronghold_crossing":
                if (CommonConfig.INJECT_CHEST_LOOT_TABLES.get()) {
                    injectChest(table, "injects/chest/common");
                }
                break;
            case "minecraft:chests/bastion_bridge":
            case "minecraft:chests/bastion_hoglin_stable":
            case "minecraft:chests/bastion_other":
            case "minecraft:chests/nether_bridge":
            case "minecraft:chests/woodland_mansion":
            case "apotheosis:chests/chest_valuable":
                if (CommonConfig.INJECT_CHEST_LOOT_TABLES.get()) {
                    injectChest(table, "injects/chest/rare");
                }
                break;
            case "minecraft:chests/ancient_city":
            case "minecraft:chests/end_city_treasure":
                if (CommonConfig.INJECT_CHEST_LOOT_TABLES.get()) {
                    injectChest(table, "injects/chest/legendary");
                }
                break;
        }
    }

    private static void injectChest(LootTable table, String name) {
        table.addPool(LootPool.lootPool()
                .add(LootTableReference.lootTableReference(ApotheosisModernRagnarok.loc(name)))
                .build());
    }

    private static void injectArcheology(LootTable table, ResourceLocation gunId) {
        LootPool pool = ((LootTableAccessor) table).getPools()
                .stream().findFirst()
                .orElse(null);

        LootPoolEntryContainer.Builder<?> entry = LootItem
                .lootTableItem(ModItems.MODERN_KINETIC_GUN.get())
                .when(GunLootFunctions.isGunInstalled(gunId))
                .apply(GunLootFunctions.initGunInfo(gunId))
                .apply(ApothReforgeFunction.apothReforge(new ResourceLocation("apotheosis:ancient")));

        if (pool == null) {
            table.addPool(LootPool.lootPool().add(entry).build());
        } else {
            var accessor = (LootPoolAccessor) pool;
            var entries = Lists.newArrayList(accessor.getEntries());
            entries.add(entry.build());
            accessor.setEntries(entries.toArray(new LootPoolEntryContainer[0]));
        }
    }
}
