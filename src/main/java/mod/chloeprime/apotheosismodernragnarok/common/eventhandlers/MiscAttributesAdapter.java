package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class MiscAttributesAdapter {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void meleeDamage(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity attacker)) {
            return;
        }
        var kun = IGun.getIGunOrNull(attacker.getMainHandItem());
        if (kun == null) {
            return;
        }
        if (!event.getSource().is(DamageTypes.PLAYER_ATTACK) && !event.getSource().is(DamageTypes.MOB_ATTACK)) {
            return;
        }
        var attribute = Attributes.ATTACK_DAMAGE;
        var exDamage = attacker.getAttributeValue(attribute) - attribute.getDefaultValue();
        event.setAmount(event.getAmount() + (float) exDamage);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void lifeStealOverheal(EntityHurtByGunEvent.Post e) {
        lifeStealOverheal0(e.getAttacker(), e.getHurtEntity(), e.getAmount());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void lifeStealOverheal(EntityKillByGunEvent e) {
        lifeStealOverheal0(e.getAttacker(), e.getKilledEntity(), e.getBaseDamage() * e.getHeadshotMultiplier());
    }

    private static void lifeStealOverheal0(@Nullable LivingEntity attacker, Entity victim, float amount) {
        if (attacker == null || attacker.level().isClientSide) {
            return;
        }
        if (!(victim instanceof LivingEntity livingVictim)) {
            return;
        }
        float lifesteal = (float) attacker.getAttributeValue(ALObjects.Attributes.LIFE_STEAL.get());
        float dmg = Math.min(amount, livingVictim.getMaxHealth());
        if (lifesteal > 0.001) {
            attacker.heal(dmg * lifesteal);
        }
        float overheal = (float) attacker.getAttributeValue(ALObjects.Attributes.OVERHEAL.get());
        float maxOverheal = attacker.getMaxHealth() * 0.5F;
        if (overheal > 0 && attacker.getAbsorptionAmount() < maxOverheal) {
            attacker.setAbsorptionAmount(Math.min(maxOverheal, attacker.getAbsorptionAmount() + dmg * overheal));
        }

    }
}
