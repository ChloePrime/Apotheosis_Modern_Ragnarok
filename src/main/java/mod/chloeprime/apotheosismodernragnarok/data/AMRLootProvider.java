package mod.chloeprime.apotheosismodernragnarok.data;

import com.tacz.guns.init.ModItems;
import mod.chloeprime.apotheosismodernragnarok.common.loot.ApothReforgeFunction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;
import static mod.chloeprime.gunsmithlib.api.common.GunLootFunctions.*;

public class AMRLootProvider {
    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(InjectSubProvider::new, LootContextParamSets.CHEST)
        ));
    }

    @ParametersAreNonnullByDefault
    public static class InjectSubProvider implements LootTableSubProvider {
        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
            // 手枪套餐
            output.accept(loc("kits/pistol/tacz/deagle"), gunAndAmmo("tacz:deagle", "tacz:50ae", 1, 18));
            output.accept(loc("kits/pistol/tacz/glock_17"), gunAndAmmo("tacz:glock_17", "tacz:9mm", 2, 50));
            output.accept(loc("kits/pistol/tacz/b93r"), gunAndAmmo("tacz:b93r", "tacz:9mm", 3, 50));
            output.accept(loc("kits/pistol/tacz/deagle_golden"), gunAndAmmo("tacz:deagle_golden", "tacz:357mag", 1, 24));
            output.accept(loc("kits/pistol/tacz/m1911"), gunAndAmmo("tacz:m1911", "tacz:45acp", 1, 50));
            output.accept(loc("kits/pistol/tacz/cz75"), gunAndAmmo("tacz:cz75", "tacz:9mm", 3, 50));
            output.accept(loc("kits/pistol/tacz/p320"), gunAndAmmo("tacz:p320", "tacz:45acp", 2, 50));

            // 狙击枪套餐
            output.accept(loc("kits/sniper/tacz/m700"), gunAndAmmo("tacz:m700", "tacz:30_06", 1, 15));
            output.accept(loc("kits/sniper/tacz/springfield1873"), gunAndAmmo("tacz:springfield1873", "tacz:45_70", 1, 20));
            output.accept(loc("kits/sniper/tacz/ai_awp"), gunAndAmmo("tacz:ai_awp", "tacz:338", 1, 30));
            output.accept(loc("kits/sniper/tacz/m107"), gunAndAmmo("tacz:m107", "tacz:50bmg", 1, 20));
            output.accept(loc("kits/sniper/tacz/m95"), gunAndAmmo("tacz:m95", "tacz:50bmg", 1, 20));

            // 步枪套餐
            output.accept(loc("kits/rifle/tacz/sks_tactical"), gunAndAmmo("tacz:sks_tactical", "tacz:762x39", 2, 30));
            output.accept(loc("kits/rifle/tacz/type_81"), gunAndAmmo("tacz:type_81", "tacz:762x39", 3, 40));
            output.accept(loc("kits/rifle/tacz/qbz_95"), gunAndAmmo("tacz:qbz_95", "tacz:58x42", 4, 50));
            output.accept(loc("kits/rifle/tacz/ak47"), gunAndAmmo("tacz:ak47", "tacz:762x39", 3, 40));
            output.accept(loc("kits/rifle/tacz/hk416d"), gunAndAmmo("tacz:hk416d", "tacz:556x45", 4, 50));
            output.accept(loc("kits/rifle/tacz/m4a1"), gunAndAmmo("tacz:m4a1", "tacz:556x45", 4, 50));
            output.accept(loc("kits/rifle/tacz/m16a1"), gunAndAmmo("tacz:m16a1", "tacz:556x45", 4, 50));
            output.accept(loc("kits/rifle/tacz/hk_g3"), gunAndAmmo("tacz:hk_g3", "tacz:308", 2, 50));
            output.accept(loc("kits/rifle/tacz/m16a4"), gunAndAmmo("tacz:m16a4", "tacz:556x45", 4, 50));
            output.accept(loc("kits/rifle/tacz/spr15hb"), gunAndAmmo("tacz:spr15hb", "tacz:556x45", 2, 50));
            output.accept(loc("kits/rifle/tacz/scar_l"), gunAndAmmo("tacz:scar_l", "tacz:556x45", 4, 50));
            output.accept(loc("kits/rifle/tacz/scar_h"), gunAndAmmo("tacz:scar_h", "tacz:308", 3, 40));
            output.accept(loc("kits/rifle/tacz/mk14"), gunAndAmmo("tacz:mk14", "tacz:308", 3, 40));
            output.accept(loc("kits/rifle/tacz/aug"), gunAndAmmo("tacz:aug", "tacz:556x45", 4, 50));
            output.accept(loc("kits/rifle/tacz/g36k"), gunAndAmmo("tacz:g36k", "tacz:556x45", 4, 50));

            // 霰弹枪套餐
            output.accept(loc("kits/shotgun/tacz/db_short"), gunAndAmmo("tacz:db_short", "tacz:12g", 1, 12));
            output.accept(loc("kits/shotgun/tacz/db_long"), gunAndAmmo("tacz:db_long", "tacz:12g", 1, 12));
            output.accept(loc("kits/shotgun/tacz/m870"), gunAndAmmo("tacz:m870", "tacz:12g", 1, 24));
            output.accept(loc("kits/shotgun/tacz/aa12"), gunAndAmmo("tacz:aa12", "tacz:12g", 6, 20));

            // 冲锋枪
            output.accept(loc("kits/smg/tacz/ump45"), gunAndAmmo("tacz:ump45", "tacz:45acp", 4, 50));
            output.accept(loc("kits/smg/tacz/vector45"), gunAndAmmo("tacz:vector45", "tacz:45acp", 4, 40));
            output.accept(loc("kits/smg/tacz/mp5a5"), gunAndAmmo("tacz:mp5a5", "tacz:9mm", 4, 50));
            output.accept(loc("kits/smg/tacz/uzi"), gunAndAmmo("tacz:uzi", "tacz:9mm", 4, 50));
            output.accept(loc("kits/smg/tacz/p90"), gunAndAmmo("tacz:p90", "tacz:57x28", 4, 50));

            // 重武器
            output.accept(loc("kits/heavy/tacz/rpg7"), gunAndAmmo("tacz:rpg7", "tacz:rpg_rocket", ConstantValue.exactly(1), UniformGenerator.between(2, 4)));
            output.accept(loc("kits/heavy/tacz/m320"), gunAndAmmo("tacz:m320", "tacz:40mm", ConstantValue.exactly(1), UniformGenerator.between(6, 12)));

            // 机枪
            output.accept(loc("kits/mg/tacz/m249"), gunAndAmmo("tacz:m249", "tacz:556x45", 8, 50));
            output.accept(loc("kits/mg/tacz/minigun"), gunAndAmmo("tacz:minigun", "tacz:308", 15, 40));
            output.accept(loc("kits/mg/tacz/rpk"), gunAndAmmo("tacz:rpk", "tacz:762x39", 8, 40));

            // 新手礼包箱子
            output.accept(loc("injects/chest/for_newcomers"), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/glock_17")))
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/m1911")))
                            .add(LootTableReference.lootTableReference(loc("kits/sniper/tacz/springfield1873")))
                            .add(LootTableReference.lootTableReference(loc("kits/shotgun/tacz/db_short")))
                            .add(LootTableReference.lootTableReference(loc("kits/shotgun/tacz/db_long")))
                    ));

            // 普通箱子
            output.accept(loc("injects/chest/common"), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/deagle")))
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/glock_17")))
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/b93r")))
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/deagle_golden")))
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/m1911")))
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/cz75")))
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/p320")))

                            .add(LootTableReference.lootTableReference(loc("kits/sniper/tacz/m700")))
                            // no springfield1873
                            // no ai_awp
                            // no m107
                            // no m95

                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/sks_tactical")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/type_81")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/qbz_95")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/ak47")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/hk416d")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/m4a1")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/m16a1")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/hk_g3")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/m16a4")))
                            // no spr15hb
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/scar_l")))
                            // no scar_h
                            // no mk14
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/aug")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/g36k")))

                            .add(LootTableReference.lootTableReference(loc("kits/shotgun/tacz/db_short")))
                            .add(LootTableReference.lootTableReference(loc("kits/shotgun/tacz/db_long")))

                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/ump45")))
                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/vector45")))
                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/mp5a5")))
                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/uzi")))
                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/p90")))
                    ));

            // 稀有箱子
            output.accept(loc("injects/chest/rare"), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/deagle")))
                            // no glock_17
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/b93r")))
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/deagle_golden")))
                            // no m1911
                            .add(LootTableReference.lootTableReference(loc("kits/pistol/tacz/cz75")))
                            // no p320

                            .add(LootTableReference.lootTableReference(loc("kits/sniper/tacz/m700")))
                            // no springfield1873
                            .add(LootTableReference.lootTableReference(loc("kits/sniper/tacz/ai_awp")))
                            // no m107
                            // no m95

                            // no sks_tactical
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/type_81")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/qbz_95")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/ak47")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/hk416d")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/m4a1")))
                            // no m16a1
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/hk_g3")))
                            // no m16a4
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/spr15hb")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/scar_l")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/scar_h")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/mk14")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/aug")))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/g36k")))

                            // no db_short
                            // no db_long
                            .add(LootTableReference.lootTableReference(loc("kits/shotgun/tacz/m870")))
                            // no aa12

                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/ump45")))
                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/vector45")))
                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/mp5a5")))
                            // no uzi
                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/p90")))

                            .add(LootTableReference.lootTableReference(loc("kits/heavy/tacz/rpg7")))
                            .add(LootTableReference.lootTableReference(loc("kits/heavy/tacz/m320")))

                            .add(LootTableReference.lootTableReference(loc("kits/mg/tacz/m249")))
                            // no minigun
                            .add(LootTableReference.lootTableReference(loc("kits/mg/tacz/rpk")))
                    ));

            // 宝藏箱子
            output.accept(loc("injects/chest/legendary"), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootTableReference.lootTableReference(loc("kits/sniper/tacz/ai_awp")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/sniper/tacz/m107")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/sniper/tacz/m95")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/spr15hb")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/scar_h")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/mk14")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/shotgun/tacz/aa12")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/smg/tacz/p90")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/mg/tacz/m249")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/mg/tacz/rpk")).setWeight(1000))
                            .add(LootTableReference.lootTableReference(loc("kits/mg/tacz/minigun"))
                                    .setWeight(-20)
                                    .setQuality(1)
                                    .apply(ApothReforgeFunction.apothReforge(new ResourceLocation("apotheosis:ancient")))
                            )
                    ));

            // 中东特色箱子
            output.accept(loc("injects/chest/middle_east"), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/sks_tactical")).setWeight(64))
                            .add(LootTableReference.lootTableReference(loc("kits/rifle/tacz/ak47")).setWeight(64))
                            .add(LootTableReference.lootTableReference(loc("kits/shotgun/tacz/db_short")).setWeight(14))
                            .add(LootTableReference.lootTableReference(loc("kits/shotgun/tacz/db_long")).setWeight(14))
                            .add(LootTableReference.lootTableReference(loc("kits/heavy/tacz/rpg7"))
                                    .setWeight(2)
                                    .setQuality(2)
                                    .apply(ApothReforgeFunction.apothReforge(new ResourceLocation("apotheosis:epic")))
                            )
                            .add(LootTableReference.lootTableReference(loc("kits/mg/tacz/rpk"))
                                    .setWeight(2)
                                    .setQuality(2)
                                    .apply(ApothReforgeFunction.apothReforge(new ResourceLocation("apotheosis:epic")))
                            )
                    ));
        }

        private LootTable.Builder gunAndAmmo(String gunId, String ammoId, int rolls, int ammoCountPerRoll) {
            return gunAndAmmo(gunId, ammoId, ConstantValue.exactly(rolls), ConstantValue.exactly(ammoCountPerRoll));
        }

        /**
         * @param rolls 别设置太高，否则会把其他战利品挤掉
         */
        private LootTable.Builder gunAndAmmo(String gunId, String ammoId, NumberProvider rolls, NumberProvider ammoCountPerRoll) {
            return LootTable.lootTable()
                    .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.MODERN_KINETIC_GUN.get())
                                            .when(isGunInstalled(new ResourceLocation(gunId)))
                                            .apply(initGunInfo(new ResourceLocation(gunId)))))
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(ModItems.AMMO.get())
                                    .when(isAmmoInstalled(new ResourceLocation(ammoId)))
                                    .apply(initAmmoInfo(new ResourceLocation(ammoId)))
                                    .apply(SetItemCountFunction.setCount(ammoCountPerRoll))
                            )
                            .setRolls(rolls));
        }
    }
}
