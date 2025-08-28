package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import dev.shadowsoffire.apothic_attributes.payload.CritParticlePayload;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

import static dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes.*;

/**
 * 修复暴击暴伤判定对子弹的 2 段伤害分别判定的问题
 */
@EventBusSubscriber
public class GunCritFix {
    private GunCritFix() {
    }

    public static final AttributeModifier AM = new AttributeModifier(
            ApotheosisModernRagnarok.loc("gun_crit_fix"),
            -2000000000, AttributeModifier.Operation.ADD_VALUE
    );

    public static final Multimap<Holder<Attribute>, AttributeModifier> AM_TABLE = ImmutableMultimap.of(CRIT_CHANCE, AM);

    @SubscribeEvent
    public static void preHurt(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        var shooter = event.getAttacker();
        var victim = event.getHurtEntity();
        if (shooter == null || victim == null || shooter.getAttributeValue(CRIT_CHANCE) <= 0) {
            return;
        }
        rollCrit(event, shooter, victim);
        shooter.getAttributes().addTransientAttributeModifiers(AM_TABLE);
    }

    private static void rollCrit(EntityHurtByGunEvent.Pre event, LivingEntity attacker, Entity victim) {
        double critChance = attacker.getAttributeValue(CRIT_CHANCE);
        double critDmg = attacker.getAttributeValue(CRIT_DAMAGE);

        RandomSource rand = event.getHurtEntity() instanceof LivingEntity le
                ? le.getRandom()
                : attacker.getRandom();

        double critMult = 1;

        // Roll for crits. Each overcrit reduces the effectiveness by 15%
        // We stop rolling when crit chance fails or the crit damage would reduce the total damage dealt.
        while (rand.nextFloat() <= critChance && critDmg > 1.0F) {
            critChance--;
            critMult *= critDmg;
            critDmg *= 0.85;
        }

        event.setBaseAmount(event.getBaseAmount() * (float) critMult);

        if (critMult > 1 && !attacker.level().isClientSide) {
            criticalFeedback(attacker, victim);
        }
    }

    public static void criticalFeedback(LivingEntity attacker, Entity victim) {
        if (attacker.level().isClientSide) {
            return;
        }
        PacketDistributor.sendToPlayersTrackingEntity(victim, new CritParticlePayload(victim.getId()));
        // 播放暴击音效
        var rand = attacker.getRandom();
        var snd = ModContent.Sounds.CRITICAL_HIT.get();
        var sndPitch = Mth.lerp(rand.nextFloat(), 0.8F, 1.25F);
        victim.playSound(snd, 1, sndPitch);
        if (attacker instanceof Player player && player.position().distanceToSqr(victim.position()) > 4 * 4) {
            player.playNotifySound(snd, victim.getSoundSource(), 1, sndPitch);
        }
    }

    @SubscribeEvent
    public static void postHurt(EntityHurtByGunEvent.Post event) {
        cleanup(event.getAttacker());
    }

    @SubscribeEvent
    public static void postKill(EntityKillByGunEvent event) {
        cleanup(event.getAttacker());
    }

    private static void cleanup(@Nullable LivingEntity shooter) {
        if (shooter == null || shooter.level().isClientSide) {
            return;
        }
        shooter.getAttributes().removeAttributeModifiers(AM_TABLE);
    }
}
