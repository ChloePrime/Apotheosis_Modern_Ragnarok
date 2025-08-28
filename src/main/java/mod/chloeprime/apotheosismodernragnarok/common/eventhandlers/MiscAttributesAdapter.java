package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;

@EventBusSubscriber
public class MiscAttributesAdapter {
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
        // 射击标靶车不触发
        if (victim.getType().is(ModContent.Tags.GUN_IMMUNE)) {
            return;
        }

        float lifesteal = (float) attacker.getAttributeValue(ALObjects.Attributes.LIFE_STEAL);
        float dmg = Math.min(amount, livingVictim.getMaxHealth());
        if (lifesteal > 0.001) {
            attacker.heal(dmg * lifesteal);
        }
        float overheal = (float) attacker.getAttributeValue(ALObjects.Attributes.OVERHEAL);
        float maxOverheal = attacker.getMaxHealth() * 0.5F;
        if (overheal > 0 && attacker.getAbsorptionAmount() < maxOverheal) {
            attacker.setAbsorptionAmount(Math.min(maxOverheal, attacker.getAbsorptionAmount() + dmg * overheal));
        }
    }
}
