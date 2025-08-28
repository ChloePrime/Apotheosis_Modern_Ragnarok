package mod.chloeprime.apotheosismodernragnarok.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mod.chloeprime.apotheosismodernragnarok.client.fx.ExecutionFeedback;
import mod.chloeprime.apotheosismodernragnarok.network.S2CExecutionFeedback;
import mod.chloeprime.apotheosismodernragnarok.network.S2CPerfectBlockTriggered;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Random;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

@EventBusSubscriber(Dist.CLIENT)
public class ClientNetworkHandler {
    private static final RandomGenerator RNG = new Random();
    private static final Minecraft MC = Minecraft.getInstance();
    private static final Int2ObjectMap<Consumer<Entity>> DEFERRED_ENTITY_EVENTS = new Int2ObjectLinkedOpenHashMap<>();

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

    public static void handleExecutionFeedback(S2CExecutionFeedback packet) {
        ExecutionFeedback.handleExecutionFeedback(packet);
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) {
            return;
        }
        var behavior = DEFERRED_ENTITY_EVENTS.remove(event.getEntity().getId());
        if (behavior != null) {
            behavior.accept(event.getEntity());
        }
    }
}
