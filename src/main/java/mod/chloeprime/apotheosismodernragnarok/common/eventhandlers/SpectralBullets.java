package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tac.guns.entity.DamageSourceProjectile;
import mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis.SpectralShotAffixAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shadows.apotheosis.Apotheosis;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.affix.effect.SpectralShotAffix;
import shadows.placebo.json.DynamicRegistryObject;

import java.util.Objects;
import java.util.Optional;

/**
 * 光灵箭 -> 点亮敌人的子弹
 * <p/>
 * @see mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis.MixinSpectralShotAffix tooltip魔改
 */
@Mod.EventBusSubscriber
public class SpectralBullets {
    public static final DynamicRegistryObject<SpectralShotAffix> SPECTRAL_AFFIX = AffixManager.INSTANCE.makeObj(Apotheosis.loc("spectral"));
    public static final int SURVIVAL_SPECTRAL_ARROW_EFFECT_DURATION = 200;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamaged(LivingDamageEvent e) {
        if (e.getEntity().getLevel().isClientSide() || !(e.getSource() instanceof DamageSourceProjectile source)) {
            return;
        }
        SPECTRAL_AFFIX.ifPresent(affix -> Optional.ofNullable(AffixHelper.getAffixes(source.getWeapon()).get(affix)).ifPresent(instance -> {
            var victim = e.getEntityLiving();
            var attacker = Optional.ofNullable(source.getEntity()).orElse(source.getDirectEntity());

            float rate = ((SpectralShotAffixAccessor) affix).invokeGetTrueLevel(instance.rarity(), instance.level());
            var diceHolder = Objects.requireNonNullElse(attacker,e.getEntity());

            if (diceHolder.getLevel().getRandom().nextFloat() < rate) {
                var effect = new MobEffectInstance(MobEffects.GLOWING, SURVIVAL_SPECTRAL_ARROW_EFFECT_DURATION, 0);
                victim.addEffect(effect, attacker);
            }
        }));
    }

    private SpectralBullets() {}
}
