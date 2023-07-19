package mod.chloeprime.apotheosismodernragnarok.common.internal;

import net.minecraft.world.damagesource.DamageSource;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface LaserProjectile {
    default void processDamageSource(DamageSource source) {
        source.setMagic();
    }
}
