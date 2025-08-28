package mod.chloeprime.apotheosismodernragnarok.common.loot;

import com.google.common.collect.Lists;
import com.tacz.guns.init.ModItems;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.LootPoolAccessor;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.LootTableAccessor;
import mod.chloeprime.gunsmithlib.api.common.GunLootFunctions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber
public class DefaultLootInjections {
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        var id = event.getName();
        var table = event.getTable();
        switch (id.toString()) {
            // 考古
            case "minecraft:archaeology/trail_ruins_rare":
                if (CommonConfig.INJECT_ARCHEOLOGY_LOOT_TABLES.get()) {
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("tacz", "springfield1873"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("tacz", "m1911"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("tacz", "db_short"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("tacz", "db_long"));
                    // 硝烟革命兼容: 手枪
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "webley"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "m1879revolver"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "coltm1851"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "sw_mk2"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "colt1873"));
                    // 硝烟革命兼容: 狙击枪
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "gew98"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "smle_mk3"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "mosin9130"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "win1894"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "type99"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "gras1874"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "m1903"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "sharps"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "martinihenry"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "lebel1886"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "berthier"));
                    // 硝烟革命兼容: 霰弹枪
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "m1887"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "one_barrel"));
                }
                break;
            case "minecraft:archaeology/desert_pyramid":
            case "minecraft:archaeology/desert_well":
                if (CommonConfig.INJECT_ARCHEOLOGY_LOOT_TABLES.get()) {
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "mosin9130"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "sks"));
                    injectArcheology(table, ResourceLocation.fromNamespaceAndPath("hamster", "one_barrel"));
                }
                break;
            // 箱子：新手礼包
            case "minecraft:chests/ruined_portal":
            case "minecraft:chests/spawn_bonus_chest":
            case "apotheosis:chests/tome_tower":
                injectChest(table, "injects/chest/for_newcomers", 1);
                break;
            // 箱子：普通箱子
            case "minecraft:chests/abandoned_mineshaft":
            case "minecraft:chests/igloo_chest":
            case "minecraft:chests/pillager_outpost":
            case "minecraft:chests/shipwreck_supply":
            case "minecraft:chests/simple_dungeon":
            case "minecraft:chests/stronghold_corridor":
            case "dungeons_arise:chests/bandit_village/bandit_village_normal":
            case "dungeons_arise:chests/bandit_village/bandit_village_supply":
            case "dungeons_arise:chests/bandit_village/bandit_village_tents":
            case "dungeons_arise:chests/illager_campsite/illager_campsite_supply":
            case "dungeons_arise:chests/illager_windmill/illager_windmill_treasure":
            case "dungeons_arise:chests/greenwood_pub/greenwood_pub_normal":
            case "dungeons_arise:chests/mechanical_nest/mechanical_nest_equipment":
            case "dungeons_arise:chests/mechanical_nest/mechanical_nest_normal":
            case "dungeons_arise:chests/mechanical_nest/mechanical_nest_supply":
            case "dungeons_arise:chests/mining_system/mining_system_treasure":
            case "dungeons_arise:chests/mushroom_house/mushroom_house_normal":
            case "dungeons_arise:chests/mushroom_house/mushroom_house_treasure":
            case "dungeons_arise:chests/mushroom_mines/mushroom_mines_tools":
            case "dungeons_arise:chests/scorched_mines/scorched_mines_housing":
            case "dungeons_arise:chests/scorched_mines/scorched_mines_hub":
            case "dungeons_arise:chests/scorched_mines/scorched_mines_normal":
            case "dungeons_arise:chests/small_prairie_house/small_prairie_house_normal":
            case "dungeons_arise:chests/small_prairie_house/small_prairie_house_ruined":
            case "dungeons_arise:chests/thornborn_towers/thornborn_towers_rooms":
            case "dungeons_arise:chests/thornborn_towers/thornborn_towers_top_rooms":
                injectChest(table, "injects/chest/common", 0.1F);
                break;
            // 箱子：普通箱子（必出）
            case "dungeons_arise:chests/lighthouse/lighthouse_top":
            case "dungeons_arise:chests/illager_galley/illager_galley_supply":
            case "dungeons_arise:chests/illager_galley/illager_galley_treasure":
                injectChest(table, "injects/chest/common", 1);
                break;
            // 箱子：稀有箱子
            case "minecraft:chests/bastion_bridge":
            case "minecraft:chests/bastion_hoglin_stable":
            case "minecraft:chests/bastion_other":
            case "minecraft:chests/nether_bridge":
            case "apotheosis:chests/chest_valuable":
            case "dungeons_arise:chests/bandit_towers/bandit_towers_gardens":
            case "dungeons_arise:chests/bandit_towers/bandit_towers_normal":
            case "dungeons_arise:chests/bandit_towers/bandit_towers_rooms":
            case "dungeons_arise:chests/bandit_towers/bandit_towers_supply":
            case "dungeons_arise:chests/illager_corsair/illager_corsair_supply":
            case "dungeons_arise:chests/keep_kayra/keep_kayra_garden_normal":
            case "dungeons_arise:chests/keep_kayra/keep_kayra_garden_treasure":
            case "dungeons_arise:chests/keep_kayra/keep_kayra_library_normal":
            case "dungeons_arise:chests/keep_kayra/keep_kayra_library_treasure":
            case "dungeons_arise:chests/keep_kayra/keep_kayra_normal":
            case "dungeons_arise:chests/keep_kayra/keep_kayra_treasure":
            case "dungeons_arise:chests/mechanical_nest/mechanical_nest_treasure":
            case "dungeons_arise:chests/plague_asylum/plague_asylum_cells":
            case "dungeons_arise:chests/plague_asylum/plague_asylum_normal":
            case "dungeons_arise:chests/plague_asylum/plague_asylum_storage":
            case "dungeons_arise:chests/scorched_mines/scorched_mines_treasure":
            case "dungeons_arise:chests/thornborn_towers/thornborn_towers_top_treasure":
                injectChest(table, "injects/chest/rare", 0.25F);
                break;
            // 箱子：稀有箱子（必出）
            case "dungeons_arise:chests/illager_corsair/illager_corsair_treasure":
                injectChest(table, "injects/chest/rare", 1);
                break;
            // 箱子：传说箱子
            case "minecraft:chests/ancient_city":
            case "minecraft:chests/end_city_treasure":
            case "dungeons_arise:chests/aviary/aviary_treasure":
            case "dungeons_arise:chests/bandit_towers/bandit_towers_treasure":
            case "dungeons_arise:chests/foundry/foundry_chains":
            case "dungeons_arise:chests/foundry/foundry_lava_pit":
            case "dungeons_arise:chests/foundry/foundry_normal":
            case "dungeons_arise:chests/foundry/foundry_passage_exterior":
            case "dungeons_arise:chests/foundry/foundry_passage_normal":
            case "dungeons_arise:chests/infested_temple/infested_temple_top_treasure":
                injectChest(table, "injects/chest/legendary", 0.25F);
                break;
            // 箱子：传说箱子（必出）
            case "minecraft:chests/bastion_treasure":
            case "minecraft:chests/woodland_mansion":
            case "dungeons_arise:chests/shiraz_palace/shiraz_palace_elite":
                injectChest(table, "injects/chest/legendary", 1);
                break;
            // 箱子：中东地区特色
            case "dungeons_arise:chests/shiraz_palace/shiraz_palace_gardens":
            case "dungeons_arise:chests/shiraz_palace/shiraz_palace_normal":
            case "dungeons_arise:chests/shiraz_palace/shiraz_palace_rooms":
            case "dungeons_arise:chests/shiraz_palace/shiraz_palace_supply":
            case "dungeons_arise:chests/shiraz_palace/shiraz_palace_towers":
            case "dungeons_arise:chests/shiraz_palace/shiraz_palace_treasure":
                injectChest(table, "injects/chest/middle_east", 0.25F);
                break;
        }
    }

    private static void injectChest(LootTable table, String name, float chance) {
        if (!CommonConfig.INJECT_CHEST_LOOT_TABLES.get()) {
            return;
        }
        LootPool.Builder pool = LootPool.lootPool();
        if (chance < 1) {
            pool = pool.when(LootItemRandomChanceCondition.randomChance(chance));
        }
        table.addPool(pool
                .add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, ApotheosisModernRagnarok.loc(name))))
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
                .apply(ApothReforgeFunction.apothReforge(ResourceLocation.parse("apotheosis:uncommon")))
                .apply(reforgeWithCondition(ResourceLocation.parse("apotheosis:rare"), 0.25F))
                .apply(reforgeWithCondition(ResourceLocation.parse("apotheosis:epic"), 0.1F));


        if (pool == null) {
            table.addPool(LootPool.lootPool().add(entry).build());
        } else {
            var accessor = (LootPoolAccessor) pool;
            var entries = Lists.newArrayList(accessor.getEntries());
            entries.add(entry.build());
            accessor.setEntries(entries);
        }
    }

    private static LootItemFunction.Builder reforgeWithCondition(ResourceLocation rarity, float chance) {
        return ApothReforgeFunction
                .apothReforge(rarity)
                .when(LootItemRandomChanceCondition.randomChance(chance));
    }
}
