package mod.chloeprime.apotheosismodernragnarok.client.fx;

import mod.chloeprime.apotheosismodernragnarok.client.internal.BoostableParticle;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.network.S2CExecutionFeedback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.random.RandomGenerator;

@EventBusSubscriber(Dist.CLIENT)
public class ExecutionFeedback {
    private static final RandomGenerator RNG = new Random();
    private static final Minecraft MC = Minecraft.getInstance();
    private static final Collection<AsyncParticlePlan> PLANS = new LinkedHashSet<>();

    private static class AsyncParticlePlan {
        @Nullable Entity victim;
        Vec3 fallbackPos;
        Vec3 normal;
        long endTime;
    }

    public static void handleExecutionFeedback(S2CExecutionFeedback packet) {
        var level = MC.level;
        if (level == null) {
            return;
        }
        var victim = level.getEntity(packet.victimId());
        var attacker = level.getEntity(packet.attackerId());
        if (victim == null) {
            return;
        }
        var normal = (attacker != null
                ? attacker.position().subtract(victim.position())
                : victim.getLookAngle())
                .with(Direction.Axis.Y, 0.1)
                .normalize();
        var position = victim.position().with(Direction.Axis.Y, victim.getY(0.6));

        var now = level.getGameTime();
        var plan = new AsyncParticlePlan();
        plan.victim = victim;
        plan.fallbackPos = position;
        plan.normal = normal;
        plan.endTime = now + 10;
        PLANS.add(plan);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        var level = MC.level;
        if (level == null) {
            PLANS.clear();
            return;
        }
        var now = level.getGameTime();
        var iterator = PLANS.iterator();
        while (iterator.hasNext()) {
            var plan = iterator.next();
            // 清理实体引用以释放内存
            if (plan.victim != null && plan.victim.isRemoved()) {
                plan.victim = null;
            }
            // 生成粒子
            var position = plan.victim != null
                    ? plan.victim.position().with(Direction.Axis.Y, plan.victim.getY(0.6))
                    : plan.fallbackPos;
            var normal = plan.normal;
            var particleCount = 5;
            for (int i = 0; i < particleCount; i++) {
                var speed = 0.4;
                var force = 1.5;
                var distribution = new Vec3(RNG.nextGaussian(), RNG.nextGaussian(), RNG.nextGaussian()).normalize().scale(0.01);
                var velocity = normal.add(distribution).normalize().scale(force);
                createBlood(position.x(), position.y(), position.z(), velocity.x(), velocity.y(), velocity.z(), speed);
            }
            // 到时间后清空生成计划
            if (now >= plan.endTime) {
                iterator.remove();
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private static void createBlood(double x, double y, double z, double dx, double dy, double dz, double speed) {
        var blood = MC.particleEngine.createParticle(ModContent.Particles.BLOOD.get(), x, y, z, dx, dy, dz);
        var force = Math.sqrt(dx * dx + dy * dy + dz * dz);
        ((BoostableParticle) blood).amr$boost(speed / force);
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static final class ParticleRegistrar {
        @SubscribeEvent
        public static void onRegisterProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModContent.Particles.BLOOD.get(), new BloodParticle.Provider());
        }
    }
}
