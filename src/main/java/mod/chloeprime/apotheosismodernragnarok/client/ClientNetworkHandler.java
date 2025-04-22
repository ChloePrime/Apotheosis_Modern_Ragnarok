package mod.chloeprime.apotheosismodernragnarok.client;

import mod.chloeprime.apotheosismodernragnarok.network.S2CPerfectBlockTriggered;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.random.RandomGenerator;

public class ClientNetworkHandler {
    private static final RandomGenerator RNG = new Random();

    public static void handlePerfectBlockTriggered(S2CPerfectBlockTriggered packet) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        var user = level.getEntity(packet.id());
        if (user == null) {
            return;
        }
        var particle = ParticleTypes.FLAME;
        var position = user.position()
                .add(user.getLookAngle().with(Direction.Axis.Y, 0).normalize().scale(0.5))
                .with(Direction.Axis.Y, user.getY(0.8));
        var particleCount = 50;
        for (int i = 0; i < particleCount; i++) {
            var speed = 0.5;
            var velocity = new Vec3(RNG.nextGaussian(), RNG.nextGaussian(), RNG.nextGaussian()).normalize().scale(speed);
            level.addParticle(particle, position.x(), position.y(), position.z(), velocity.x(), velocity.y(), velocity.z());
        }
    }
}
