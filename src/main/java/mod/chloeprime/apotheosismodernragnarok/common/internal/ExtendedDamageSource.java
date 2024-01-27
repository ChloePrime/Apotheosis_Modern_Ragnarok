package mod.chloeprime.apotheosismodernragnarok.common.internal;

import net.minecraft.world.item.ItemStack;

public interface ExtendedDamageSource {
    ItemStack apotheosis_modern_ragnarok$getWeapon();
    void apotheosis_modern_ragnarok$setWeapon(ItemStack weapon);
    boolean apotheosis_modern_ragnarok$isGunshot();
    void apotheosis_modern_ragnarok$setGunshot(boolean value);
    boolean apotheosis_modern_ragnarok$isGunshotFirstPart();
    void apotheosis_modern_ragnarok$setGunshotFirstPart(boolean value);
    boolean apotheosis_modern_ragnarok$isHeadshot();
    void apotheosis_modern_ragnarok$setHeadshot(boolean value);
}
