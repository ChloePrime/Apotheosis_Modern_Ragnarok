package mod.chloeprime.apotheosismodernragnarok.common.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EffectHelper {
    public static void createSurroundingParticles(
            ParticleOptions particle,
            LivingEntity owner,
            int count, Vec3 motion
    ) {
        var level = owner.level();
        var hitbox = owner.getBoundingBox();
        var x = owner.getX();
        var y = owner.getY() + hitbox.getYsize() / 2;
        var z = owner.getZ();
        var dx = hitbox.getXsize() / 2 + 0.125;
        var dy = hitbox.getYsize() / 2 + 0.125;
        var dz = hitbox.getZsize() / 2 + 0.125;
        var rng = owner.getRandom();
        if (level.isClientSide) {
            for (int i = 0; i < count; i++) {
                var ax = x + dx * (rng.nextFloat() - rng.nextFloat());
                var ay = y + dy * (rng.nextFloat() - rng.nextFloat());
                var az = z + dz * (rng.nextFloat() - rng.nextFloat());
                level.addParticle(particle, ax, ay, az, motion.x(), motion.y(), motion.z());
            }
        } else {
            var serverLevel = (ServerLevel) level;
            for (int i = 0; i < count; i++) {
                var ax = x + dx * (rng.nextFloat() - rng.nextFloat());
                var ay = y + dy * (rng.nextFloat() - rng.nextFloat());
                var az = z + dz * (rng.nextFloat() - rng.nextFloat());
                var speed = motion.length();
                var dir = speed == 0 ? Vec3.ZERO : motion.scale(1 / speed);
                serverLevel.sendParticles(particle, ax, ay, az, 0, dir.x(), dir.y(), dir.z(), speed);
            }
        }
    }
}
