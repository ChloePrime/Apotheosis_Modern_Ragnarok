package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tac.guns.entity.DamageSourceProjectile;
import mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis.PotionAffixAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.effect.PotionAffix;

@Mod.EventBusSubscriber
public class ApplyPotionAffixOnGunHit {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingFinallyHurt(LivingDamageEvent e) {
        if (!(e.getSource() instanceof DamageSourceProjectile source)) {
            return;
        }
        var gun = source.getWeapon();
        AffixHelper.getAffixes(gun).forEach((affix0, instance) -> {
            if (!(affix0 instanceof PotionAffixAccessor affix)) {
                return;
            }
            if (affix.getTarget() == PotionAffix.Target.ARROW_SELF && e.getSource().getEntity() instanceof LivingEntity livingAttacker) {
                // ARROW_SELF
                PotionAffix.EffectInst effect = affix.getEffects().get(instance.rarity());
                affix.invokeApplyEffect(livingAttacker, effect, instance.level());
            } else if (affix.getTarget() == PotionAffix.Target.ARROW_TARGET) {
                // ARROW_TARGET
                LivingEntity livingVictim = e.getEntityLiving();
                PotionAffix.EffectInst effect = affix.getEffects().get(instance.rarity());
                affix.invokeApplyEffect(livingVictim, effect, instance.level());
            }
        });
    }
}
