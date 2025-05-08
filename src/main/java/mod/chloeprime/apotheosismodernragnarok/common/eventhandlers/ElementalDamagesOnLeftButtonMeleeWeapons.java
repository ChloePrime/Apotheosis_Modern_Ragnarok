package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ElementalDamagesOnLeftButtonMeleeWeapons {
    public static final TagKey<DamageType> SPREAD_DAMAGE_ON_LEFT_BUTTON_MELEES = TagKey.create(
            Registries.DAMAGE_TYPE, ApotheosisModernRagnarok.loc("bugfix/spread_damage_on_left_button_melees")
    );

    public static final TagKey<DamageType> ARMOR_PIERCING_PARTS = TagKey.create(
            Registries.DAMAGE_TYPE, ApotheosisModernRagnarok.loc("bugfix/armor_piercing_parts")
    );

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void spreadElementalDamages(LivingHurtEvent event) {
        if (!event.getSource().is(SPREAD_DAMAGE_ON_LEFT_BUTTON_MELEES)) {
            return;
        }
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (GunPredicate.isDedicatedTaCZMeleeWeapon(weapon)) {
                event.setAmount(event.getAmount() * (float) GunPredicate.getBuffCoefficient(weapon));
            }
        }
    }

    public static boolean isFirstPartOfDuplicateDamage(DamageSource source) {
        return !source.is(ARMOR_PIERCING_PARTS);
    }
}
