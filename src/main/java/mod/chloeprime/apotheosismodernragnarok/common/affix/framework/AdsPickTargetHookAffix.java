package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public interface AdsPickTargetHookAffix {
    default boolean isAdsPickEnabled() {
        return true;
    }
    default void onAimingAtEntity(ItemStack stack, AffixInstance instance, EntityHitResult target) {}
}
