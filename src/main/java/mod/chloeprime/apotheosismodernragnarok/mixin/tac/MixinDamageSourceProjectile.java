package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.entity.DamageSourceProjectile;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDsp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = DamageSourceProjectile.class, remap = false)
public class MixinDamageSourceProjectile implements ExtendedDsp {
    @Unique
    private boolean apotheosis_modern_ragnarok$headshot;

    @Override
    public boolean apotheosis_modern_ragnarok$isHeadshot() {
        return apotheosis_modern_ragnarok$headshot;
    }

    @Override
    public void apotheosis_modern_ragnarok$setHeadshot(boolean value) {
        apotheosis_modern_ragnarok$headshot = value;
    }
}
