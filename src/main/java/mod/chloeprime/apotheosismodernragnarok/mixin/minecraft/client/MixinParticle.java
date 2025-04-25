package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client;

import mod.chloeprime.apotheosismodernragnarok.client.internal.BoostableParticle;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Particle.class)
public abstract class MixinParticle implements BoostableParticle {
    @Override
    public void amr$boost(double factor) {
        xd *= factor;
        yd *= factor;
        zd *= factor;
    }

    @Shadow protected double xd;
    @Shadow protected double yd;
    @Shadow protected double zd;
}
