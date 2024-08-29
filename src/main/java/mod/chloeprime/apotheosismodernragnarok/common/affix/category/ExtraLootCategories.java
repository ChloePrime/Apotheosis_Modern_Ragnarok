package mod.chloeprime.apotheosismodernragnarok.common.affix.category;

import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ExtraLootCategories {
    public static LootCategory SHOTGUN;
    public static LootCategory FULL_AUTO;
    public static LootCategory SEMI_AUTO;
    public static LootCategory BOLT_ACTION;

    public static Set<LootCategory> all() {
        return Collections.unmodifiableSet(ALL_GUNS);
    }

    public static boolean isGun(LootCategory category) {
        return ALL_GUNS.contains(category);
    }

    public static void init() {
        BOLT_ACTION = register("bolt_action", GunPredicate.matchIndex(index -> index.getGunData().getBolt() == Bolt.MANUAL_ACTION), EquipmentSlot.MAINHAND);
        SHOTGUN     = register("shotgun",     GunPredicate.matchIndex(index -> index.getBulletData().getBulletAmount() > 4), EquipmentSlot.MAINHAND);
        FULL_AUTO   = register("full_auto",   GunPredicate.supports(FireMode.AUTO), EquipmentSlot.MAINHAND);
        SEMI_AUTO   = register("semi_auto",   GunPredicate.supports(FireMode.SEMI, FireMode.BURST), EquipmentSlot.MAINHAND);
    }

    private static final Set<LootCategory> ALL_GUNS = new LinkedHashSet<>(8);

    private static LootCategory register(String path, GunPredicate predicate, EquipmentSlot... slots) {
        var registered = LootCategory.register(null, ApotheosisModernRagnarok.loc(path).toString(), predicate, slots);
        ALL_GUNS.add(registered);
        return registered;
    }
}
