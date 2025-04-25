package mod.chloeprime.apotheosismodernragnarok.client.fx;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Copied From HitFeedback project.
 * MIT licenced ©2024 ChloePrime
 */
public class BloodParticle extends SimpleTexturedParticle {
    public BloodParticle(SpriteSet sprite, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
        super(sprite, clientLevel, d, e, f, g, h, i);
        this.lifetime += random.nextInt(40, 100);
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static class Provider extends ProviderFactory<SimpleParticleType> {
        @Override
        public Particle createParticle(SpriteSet sprites, SimpleParticleType options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new BloodParticle(sprites, level, x, y, z, dx, dy, dz);
        }
    }
}
