package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public interface AdsPickTargetHookAffix {
    default boolean isAdsPickEnabled() {
        return true;
    }
    default void onAimingAtEntity(ItemStack stack, Player gunner, AffixInstance instance, EntityHitResult target) {}
}
