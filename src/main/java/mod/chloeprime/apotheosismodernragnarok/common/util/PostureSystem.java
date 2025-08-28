package mod.chloeprime.apotheosismodernragnarok.common.util;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Optional;

import static mod.chloeprime.apotheosismodernragnarok.common.CommonConfig.*;

/**
 * 架势系统
 */
@EventBusSubscriber
public class PostureSystem {
    public static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            ApotheosisModernRagnarok.loc("posture_broken"),
            -100_0000, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    public static void onAttackBeingBlocked(LivingEntity attacker) {
        var addend = 1.0 / (isBoss(attacker) ? MAX_POSTURE_FOR_BOSSES : MAX_POSTURE_FOR_MINIONS).get();
        setPosture(attacker, getPosture(attacker) + addend);
    }

    public static double getPosture(Entity entity) {
        return entity.getData(ModContent.SinceMC1211.DataAttachments.POSTURE);
    }

    public static void setPosture(Entity entity, double value) {
        if (value > entity.getData(ModContent.SinceMC1211.DataAttachments.POSTURE)) {
            var now = entity.level().getGameTime();
            var delay = isBoss(entity) ? 200 : 100;
            entity.setData(ModContent.SinceMC1211.DataAttachments.POSTURE_RECOVER_START_TIME, now + delay);
        }
        var clampedValue = Mth.clamp(value, 0, 1);
        entity.setData(ModContent.SinceMC1211.DataAttachments.POSTURE, clampedValue);

        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        Optional.ofNullable(living.getAttribute(Attributes.MOVEMENT_SPEED)).ifPresent(moveSpeed -> {
            if (isPostureBroken(clampedValue)) {
                if (!moveSpeed.hasModifier(SPEED_MODIFIER.id())) {
                    moveSpeed.addTransientModifier(SPEED_MODIFIER);
                }
            } else {
                if (moveSpeed.hasModifier(SPEED_MODIFIER.id())) {
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

    public static boolean isBoss(Entity entity) {
        return entity.getType().is(Tags.EntityTypes.BOSSES);
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        var entity = event.getEntity();
        if (!entity.isAlive()) {
            return;
        }
        var now = entity.level().getGameTime();
        if (now < entity.getData(ModContent.SinceMC1211.DataAttachments.POSTURE_RECOVER_START_TIME)) {
            return;
        }
        var posture = getPosture(entity);
        if (posture <= 0) {
            return;
        }
        var recoverSpeed = 1.0 / (isBoss(entity) ? 200 : 20);
        setPosture(entity, posture - recoverSpeed);
    }
}
