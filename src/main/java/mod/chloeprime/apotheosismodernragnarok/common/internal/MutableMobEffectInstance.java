package mod.chloeprime.apotheosismodernragnarok.common.internal;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface MutableMobEffectInstance {
    void amr$modifyDuration(Int2IntFunction code);
}
