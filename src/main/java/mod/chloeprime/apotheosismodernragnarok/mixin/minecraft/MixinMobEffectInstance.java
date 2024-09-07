package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import mod.chloeprime.apotheosismodernragnarok.common.internal.MutableMobEffectInstance;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(MobEffectInstance.class)
public abstract class MixinMobEffectInstance implements MutableMobEffectInstance {
    @Override
    public void amr$modifyDuration(Int2IntFunction code) {
        if (this.hiddenEffect != null) {
            ((MixinMobEffectInstance) (Object) this.hiddenEffect).amr$modifyDuration(code);
        }
        if (!isInfiniteDuration()) {
            duration = Math.max(1, code.applyAsInt(duration));
        }
    }

    @Shadow private int duration;
    @Shadow @Nullable private MobEffectInstance hiddenEffect;
    @Shadow public abstract boolean isInfiniteDuration();
}
