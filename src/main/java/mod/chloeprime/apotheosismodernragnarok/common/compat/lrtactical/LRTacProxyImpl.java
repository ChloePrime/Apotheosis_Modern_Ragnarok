package mod.chloeprime.apotheosismodernragnarok.common.compat.lrtactical;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import me.xjqsh.lrtactical.api.item.IMeleeWeapon;
import me.xjqsh.lrtactical.api.melee.MeleeAction;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

class LRTacProxyImpl {
    public static Optional<LootCategory> getLRTacMeleeType(ItemStack stack) {
        return stack.getItem() instanceof IMeleeWeapon melee
                ? Optional.of(isHeavyWeapon(melee, stack) ? LootCategory.HEAVY_WEAPON : LootCategory.SWORD)
                : Optional.empty();
    }

    private static boolean isHeavyWeapon(IMeleeWeapon item, ItemStack stack) {
        var action = MeleeAction.LEFT;
        var attackInfo = item.getMeleeIndex(stack)
                .map((index) -> index.getData().getAttackInfo())
                .map(combatData -> combatData.getAttackInfo(action))
                .orElse(null);
        if (attackInfo == null) {
            return false;
        }
        return attackInfo.cooldown() >= CommonConfig.LRTAC_HEAVY_WEAPON_COOLDOWN_THRESHOLD.get();
    }
}
