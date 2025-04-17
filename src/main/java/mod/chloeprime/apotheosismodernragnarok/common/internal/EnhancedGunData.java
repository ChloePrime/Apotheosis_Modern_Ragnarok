package mod.chloeprime.apotheosismodernragnarok.common.internal;

import mod.chloeprime.apotheosismodernragnarok.common.gunpack.GunApothData;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public interface EnhancedGunData {
    Optional<GunApothData> amr$getApothData();
}
