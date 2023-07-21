package mod.chloeprime.apotheosismodernragnarok.common.internal;

import net.minecraft.world.damagesource.DamageSource;

public interface MagicProjectile {
    default void processDamageSource(DamageSource source) {
        source.setMagic();
    }
}
