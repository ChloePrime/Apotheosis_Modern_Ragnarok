package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;


import mod.chloeprime.apotheosismodernragnarok.common.affix.content.ExplosionOnHeadshotAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shadows.apotheosis.adventure.affix.AffixHelper;

import java.util.Optional;

@Mod.EventBusSubscriber
public class HeadshotHandler {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingAttackEvent e) {
        var source = e.getSource();
        if (e.getEntity().getLevel().isClientSide() || !DamageUtils.isGunShot(source) || !DamageUtils.isHeadshot(source)) {
            return;
        }
        var affixes = AffixHelper.getAffixes(DamageUtils.getWeapon(source));
        ExplosionOnHeadshotAffix.INSTANCE.ifPresent(affix ->
                Optional.ofNullable(affixes.get(affix))
                        .ifPresent(instance -> affix.onHeadshot(e.getEntityLiving(), source, e.getAmount(), instance))
        );
    }

    private HeadshotHandler() {}
}
