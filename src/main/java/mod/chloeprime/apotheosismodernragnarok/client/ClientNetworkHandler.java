package mod.chloeprime.apotheosismodernragnarok.client;

import mod.chloeprime.apotheosismodernragnarok.client.fx.ExecutionFeedback;
import mod.chloeprime.apotheosismodernragnarok.common.internal.PostureHolder;
import mod.chloeprime.apotheosismodernragnarok.network.S2CExecutionFeedback;
import mod.chloeprime.apotheosismodernragnarok.network.S2CPerfectBlockTriggered;
import mod.chloeprime.apotheosismodernragnarok.network.S2CSyncStartRecoverPostureTime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.random.RandomGenerator;

public class ClientNetworkHandler {
    private static final RandomGenerator RNG = new Random();
    private static final Minecraft MC = Minecraft.getInstance();

    public static void handlePerfectBlockTriggered(S2CPerfectBlockTriggered packet) {
        var level = MC.level;
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

    public static void handleSyncStartRecoverPostureTime(S2CSyncStartRecoverPostureTime packet) {
        if (MC.level != null && MC.level.getEntity(packet.id()) instanceof PostureHolder holder) {
            holder.amr$setStartRecoverPostureTime(packet.eta());
        }
    }

    public static void handleExecutionFeedback(S2CExecutionFeedback packet) {
        ExecutionFeedback.handleExecutionFeedback(packet);
    }
}
