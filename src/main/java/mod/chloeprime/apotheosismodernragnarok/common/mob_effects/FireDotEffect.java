package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import com.google.common.collect.ImmutableList;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.util.EffectHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public class FireDotEffect extends MobEffect {
    public static final List<ItemStack> CURES = ImmutableList.of();

    public FireDotEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static FireDotEffect create() {
        return new FireDotEffect(MobEffectCategory.HARMFUL, new Color(255, 128, 0, 255).getRGB());
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 5 == 0;
    }

    @SubscribeEvent
    public void onEffectApplied(MobEffectEvent.Added event) {
        var instance = event.getEffectInstance();
        if (instance.getEffect() != this) {
            return;
        }
        var fireTicks = instance.isInfiniteDuration() ? Integer.MAX_VALUE : instance.getDuration();
        event.getEntity().setRemainingFireTicks(fireTicks);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return CURES;
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity owner, int pAmplifier) {
        if (!owner.level().isClientSide) {
            if (owner.fireImmune()) {
                owner.removeEffect(this);
                return;
            }
            var freeze = ModContent.MobEffects.FREEZE.get();
            if (owner.hasEffect(freeze)) {
                owner.removeEffect(this);
                owner.removeEffect(freeze);
                owner.playSound(SoundEvents.FIRE_EXTINGUISH);
                createSmoke(owner);
                return;
            }
        }
        var instance = owner.getEffect(this);
        if (instance == null) {
            return;
        }
        var fireTicks = instance.isInfiniteDuration() ? Integer.MAX_VALUE : instance.getDuration();
        owner.setRemainingFireTicks(fireTicks);
    }

    private static final Vec3 PARTICLE_MOTION = new Vec3(0, 0.125, 0);
    public static void createSmoke(LivingEntity owner) {
        EffectHelper.createSurroundingParticles(ParticleTypes.LARGE_SMOKE, owner, 8, PARTICLE_MOTION);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide || !event.getSource().is(DamageTypes.ON_FIRE)) {
            return;
        }
        var instance = event.getEntity().getEffect(this);
        if (instance == null) {
            return;
        }
        event.setAmount(event.getAmount() * (instance.getAmplifier() + 1));
    }
}
