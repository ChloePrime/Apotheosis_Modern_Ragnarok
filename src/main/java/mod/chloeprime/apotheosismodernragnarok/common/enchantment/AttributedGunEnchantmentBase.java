package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;

@Mod.EventBusSubscriber
public abstract class AttributedGunEnchantmentBase extends Enchantment {
    protected AttributedGunEnchantmentBase(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    public abstract void addAttributes(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> addModifierMethod, EquipmentSlot slot);

    @SubscribeEvent
    public static void onGetAttributeModifiers(ItemAttributeModifierEvent event) {
        var gun = Gunsmith.getGunInfo(event.getItemStack()).orElse(null);
        if (gun == null) {
            return;
        }
        for (var entry : gun.gunStack().getAllEnchantments().entrySet()) {
            if (entry.getKey() instanceof AttributedGunEnchantmentBase enchantment) {
                enchantment.addAttributes(gun.gunStack(), entry.getValue(), event::addModifier, event.getSlotType());
            }
        }
    }
}
