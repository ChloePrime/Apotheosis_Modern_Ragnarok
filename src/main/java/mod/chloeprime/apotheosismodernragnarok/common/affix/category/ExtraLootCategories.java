package mod.chloeprime.apotheosismodernragnarok.common.affix.category;

import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import dev.shadowsoffire.apothic_attributes.modifiers.EntitySlotGroup;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

@EventBusSubscriber
@SuppressWarnings("UnstableApiUsage")
public class ExtraLootCategories {
    private static final Set<Holder<LootCategory>> ALL_GUNS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static HolderSet<LootCategory> ALL_GUNS_HOLDER_SET;

    public static final DeferredRegister<LootCategory> DFR = DeferredRegister.create(Apoth.BuiltInRegs.LOOT_CATEGORY, ApotheosisModernRagnarok.MOD_ID);
    public static Supplier<LootCategory> SHOTGUN = register("shotgun", GunPredicate.matchIndex(index -> "shotgun".equals(index.getType())), ALObjects.EquipmentSlotGroups.MAINHAND);
    public static Supplier<LootCategory> FULL_AUTO = register("full_auto", GunPredicate.supports(FireMode.AUTO), ALObjects.EquipmentSlotGroups.MAINHAND);
    public static Supplier<LootCategory> SEMI_AUTO = register("semi_auto", GunPredicate.supports(FireMode.SEMI, FireMode.BURST), ALObjects.EquipmentSlotGroups.MAINHAND);
    public static Supplier<LootCategory> BOLT_ACTION = register("bolt_action", GunPredicate.matchIndex(ExtraLootCategories::isBoltAction).and(ExtraLootCategories::isBoltActionShotgunBoltAction), ALObjects.EquipmentSlotGroups.MAINHAND);

    public static HolderSet<LootCategory> all() {
        return Objects.requireNonNull(ALL_GUNS_HOLDER_SET, "Accessing ExtraLootCategories.all() before registration finished");
    }

    public static boolean isGun(LootCategory category) {
        return ALL_GUNS.contains(Apoth.BuiltInRegs.LOOT_CATEGORY.wrapAsHolder(category));
    }

    public static void init() {
    }

    public static boolean isBoltAction(CommonGunIndex index) {
        if (index.getGunData().getBolt() == Bolt.MANUAL_ACTION) {
            return true;
        }
        // 让EMX莫尔斯类的单发装弹武器不被判断成全自动
        return ((index.getGunData().getBolt() == Bolt.OPEN_BOLT ? 0 : 1) + index.getGunData().getAmmoAmount()) == 1;
    }

    private static boolean isBoltActionShotgunBoltAction(ItemStack stack) {
        if (!SHOTGUN.get().isValid(stack)) {
            return true;
        }
        return CommonConfig.BOLT_ACTION_SHOTGUN_IS_BOLT_ACTION.get();
    }

    private static Supplier<LootCategory> register(String path, Predicate<ItemStack> predicate, EntitySlotGroup slots) {
        var holder = DFR.register(path, () -> new LootCategory(predicate, slots));
        ALL_GUNS.add(holder);
        return holder;
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ALL_GUNS_HOLDER_SET = HolderSet.direct(ALL_GUNS.stream().toList()));
    }
}
