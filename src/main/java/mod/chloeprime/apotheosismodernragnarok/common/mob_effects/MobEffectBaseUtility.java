package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MobEffectBaseUtility extends MobEffect {
    protected MobEffectBaseUtility(MobEffectCategory category, int color) {
        super(category, color);
    }

    protected MobEffectBaseUtility(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }

    public Holder<MobEffect> holder() {
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this);
    }
}
