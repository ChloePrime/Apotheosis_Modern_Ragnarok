package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@EventBusSubscriber
public class EmergencyProtectorEnchantment {
    @SubscribeEvent
    private static void onItemAttribute(ItemAttributeModifierEvent event) {
        var gun = Gunsmith.getGunInfo(event.getItemStack()).orElse(null);
        if (gun == null || gun.getTotalAmmo() > 0) {
            return;
        }
        var slot = EquipmentSlotGroup.MAINHAND;
        EnchantmentHelper.runIterationOnItem(gun.gunStack(), (enchantment, level) -> enchantment
                .value()
                .getEffects(ModContent.SinceMC1211.EnchantmentEffectComponents.ATTRIBUTES_WHEN_AMMO_EMPTY.get())
                .forEach(effect -> {
                    if (enchantment.value().definition().slots().contains(slot)) {
                        event.addModifier(effect.attribute(), effect.getModifier(level, slot), slot);
                    }
                }));
    }

    private EmergencyProtectorEnchantment() {
    }
}
