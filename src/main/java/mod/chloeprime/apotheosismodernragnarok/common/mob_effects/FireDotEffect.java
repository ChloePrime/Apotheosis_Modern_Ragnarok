package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.util.EffectHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Set;

public class FireDotEffect extends MobEffectBaseUtility {
    public FireDotEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        NeoForge.EVENT_BUS.register(this);
    }

    public static FireDotEffect create() {
        return new FireDotEffect(MobEffectCategory.HARMFUL, new Color(255, 128, 0, 255).getRGB());
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return pDuration % 5 == 0;
    }

    @SubscribeEvent
    public final void onEffectApplied(MobEffectEvent.Added event) {
        var instance = event.getEffectInstance();
        if (instance.getEffect().value() != this) {
            return;
        }
        var fireTicks = instance.isInfiniteDuration() ? Integer.MAX_VALUE : instance.getDuration();
        event.getEntity().setRemainingFireTicks(fireTicks);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
    }

    @Override
    public boolean applyEffectTick(@Nonnull LivingEntity owner, int pAmplifier) {
        Holder<MobEffect> thisHolder = holder();
        if (!owner.level().isClientSide) {
            if (owner.fireImmune()) {
                return false;
            }
            var freeze = ModContent.MobEffects.FREEZE;
            if (owner.hasEffect(freeze)) {
                owner.removeEffect(freeze);
                owner.playSound(SoundEvents.FIRE_EXTINGUISH);
                createSmoke(owner);
                return false;
            }
        }
        var instance = owner.getEffect(thisHolder);
        if (instance == null) {
            return false;
        }
        var fireTicks = instance.isInfiniteDuration() ? Integer.MAX_VALUE : instance.getDuration();
        owner.setRemainingFireTicks(fireTicks);
        return true;
    }

    public static final Vec3 SMOKE_MOTION = new Vec3(0, 0.125, 0);
    public static void createSmoke(LivingEntity owner) {
        EffectHelper.createSurroundingParticles(ParticleTypes.LARGE_SMOKE, owner, 8, SMOKE_MOTION);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public final void onLivingHurt(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide || !event.getSource().is(DamageTypes.ON_FIRE)) {
            return;
        }
        var instance = event.getEntity().getEffect(holder());
        if (instance == null) {
            return;
        }
        event.setAmount(event.getAmount() * (instance.getAmplifier() + 1));
    }
}
