package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class AttributedGunEnchantmentBase extends Enchantment {
    protected AttributedGunEnchantmentBase(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    public abstract void addAttributes(int level, Multimap<Attribute, AttributeModifier> table, EquipmentSlot slot);
}
