package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.util.EffectHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public class FreezeEffect extends MobEffect {
    public static final UUID SPEED_MODIFIER_UUID = UUID.fromString("42d5dcd6-c8a4-442b-b57b-0c7517d4ddaa");
    public static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("833ca9ad-b489-4da3-a67d-54033b148353");
    public static final int FROZEN_THRESHOLD = 5;
    public static final String PDKEY_NO_AI_BEFORE = ApotheosisModernRagnarok.loc("no_ai_before").toString();

    public FreezeEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 2 == 0;
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity owner, int amplifier) {
        if (amplifier < FROZEN_THRESHOLD) {
            if (owner.isOnFire()) {
                owner.extinguishFire();
                owner.removeEffect(this);
                return;
            }
            var rate = (amplifier + 1) / 5F;
            if (!owner.level().isClientSide && owner.getRandom().nextFloat() <= rate) {
                EffectHelper.createSurroundingParticles(ParticleTypes.SNOWFLAKE, owner, 1, Vec3.ZERO);
            }
        } else {
            if (owner.isOnFire()) {
                owner.extinguishFire();
            }
        }
    }

    @SubscribeEvent
    public void onAdded(MobEffectEvent.Added event) {
        if (event.getEntity().isOnFire()) {
            event.getEntity().extinguishFire();
        }
        var instance = event.getEffectInstance();
        if (instance.getEffect() != this || instance.getAmplifier() < FROZEN_THRESHOLD) {
            return;
        }
        if (event.getEntity() instanceof Mob mob) {
            if (!mob.getPersistentData().contains(PDKEY_NO_AI_BEFORE)) {
                mob.getPersistentData().putBoolean(PDKEY_NO_AI_BEFORE, mob.isNoAi());
            }
            mob.setNoAi(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect() != this) {
            return;
        }
        onRemovedOrExpired(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExpired(MobEffectEvent.Expired event) {
        if (Objects.requireNonNull(event.getEffectInstance()).getEffect() != this) {
            return;
        }
        onRemovedOrExpired(event);
    }

    private void onRemovedOrExpired(MobEffectEvent event) {
        var owner = event.getEntity();
        if (owner instanceof Mob mob && owner.getPersistentData().contains(PDKEY_NO_AI_BEFORE, Tag.TAG_BYTE)) {
            mob.setNoAi(owner.getPersistentData().getBoolean(PDKEY_NO_AI_BEFORE));
            mob.getPersistentData().remove(PDKEY_NO_AI_BEFORE);
        }
    }

    public static FreezeEffect create() {
        return (FreezeEffect) new FreezeEffect(MobEffectCategory.HARMFUL, new Color(128, 255, 255, 255).getRGB())
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_UUID.toString(), -0.19, AttributeModifier.Operation.MULTIPLY_TOTAL)
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_UUID.toString(), -0.19, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
