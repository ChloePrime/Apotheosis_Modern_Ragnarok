package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;


import com.tac.guns.entity.DamageSourceProjectile;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.ExplosionOnHeadshotAffix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDsp;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shadows.apotheosis.adventure.affix.AffixHelper;

import java.util.Optional;

@Mod.EventBusSubscriber
public class HeadshotHandler {

    public static boolean isHeadshot(DamageSourceProjectile dsp) {
        return ((ExtendedDsp) dsp).apotheosis_modern_ragnarok$isHeadshot();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent e) {
        if (e.getEntity().getLevel().isClientSide() || !(e.getSource() instanceof DamageSourceProjectile source) || !isHeadshot(source)) {
            return;
        }
        var affixes = AffixHelper.getAffixes(source.getWeapon());
        ExplosionOnHeadshotAffix.INSTANCE.ifPresent(affix ->
                Optional.ofNullable(affixes.get(affix)).ifPresent(
                        instance -> affix.onHeadshot(e.getEntityLiving(), source, e.getAmount(), instance)
                )
        );
    }

    private HeadshotHandler() {}
}
