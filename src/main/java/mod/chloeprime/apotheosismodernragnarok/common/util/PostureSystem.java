package mod.chloeprime.apotheosismodernragnarok.common.util;

import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.internal.PostureHolder;
import mod.chloeprime.apotheosismodernragnarok.network.ModNetwork;
import mod.chloeprime.apotheosismodernragnarok.network.S2CSyncStartRecoverPostureTime;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * 架势系统
 */
@Mod.EventBusSubscriber
public class PostureSystem {
    public static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            UUID.fromString("efecb354-3f16-4c56-a339-9a086e004151"),
            "Posture Broken", -100_0000, AttributeModifier.Operation.MULTIPLY_TOTAL);

    public static void onAttackBeingBlocked(LivingEntity attacker) {
        var addend = isBoss(attacker) ? 0.2 : 1;
        setPosture(attacker, getPosture(attacker) + addend);
    }

    public static double getPosture(LivingEntity entity) {
        return entity.getAttributeBaseValue(ModContent.Attributes.POSTURE.get());
    }

    public static void setPosture(LivingEntity entity, double value) {
        var instance = Objects.requireNonNull(entity.getAttribute(ModContent.Attributes.POSTURE.get()));
        if (value > instance.getBaseValue()) {
            var now = entity.level().getGameTime();
            var delay = isBoss(entity) ? 200 : 100;
            ((PostureHolder) entity).amr$setStartRecoverPostureTime(now + delay);
            if (!entity.level().isClientSide) {
                ModNetwork.sendToNearby(new S2CSyncStartRecoverPostureTime(entity.getId(), now + delay), entity);
            }
        }
        var clampedValue = Mth.clamp(value, 0, 1);
        instance.setBaseValue(clampedValue);

        Optional.ofNullable(entity.getAttribute(Attributes.MOVEMENT_SPEED)).ifPresent(moveSpeed -> {
            if (isPostureBroken(clampedValue)) {
                if (!moveSpeed.hasModifier(SPEED_MODIFIER)) {
                    moveSpeed.addTransientModifier(SPEED_MODIFIER);
                }
            } else {
                if (moveSpeed.hasModifier(SPEED_MODIFIER)) {
                    moveSpeed.removeModifier(SPEED_MODIFIER);
                }
            }
        });
    }

    public static boolean isPostureBroken(LivingEntity entity) {
        return isPostureBroken(getPosture(entity));
    }

    public static boolean isPostureBroken(double posture) {
        return posture >= 0.98;
    }

    public static boolean isBoss(LivingEntity entity) {
        return entity.getType().is(Tags.EntityTypes.BOSSES);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        var entity = event.getEntity();
        if (!entity.isAlive()) {
            return;
        }
        var now = entity.level().getGameTime();
        if (now < ((PostureHolder) entity).amr$getStartRecoverPostureTime()) {
            return;
        }
        var posture = getPosture(entity);
        if (posture <= 0) {
            return;
        }
        var recoverSpeed = 1.0 / (isBoss(entity) ? 200 : 20);
        setPosture(entity, posture - recoverSpeed);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class AttributeAttacher {
        @SubscribeEvent
        public static void onAttachAttributes(EntityAttributeModificationEvent event) {
            event.getTypes().forEach((et) -> {
                Objects.requireNonNull(event);
                addAll(et, event::add, ModContent.Attributes.POSTURE);
            });
        }

        @SafeVarargs
        private static void addAll(EntityType<? extends LivingEntity> type, BiConsumer<EntityType<? extends LivingEntity>, Attribute> add, RegistryObject<? extends Attribute>... attributes) {
            for (RegistryObject<? extends Attribute> a : attributes) {
                add.accept(type, a.get());
            }
        }
    }
}
