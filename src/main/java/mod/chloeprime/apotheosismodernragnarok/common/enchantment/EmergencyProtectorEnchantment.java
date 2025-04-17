package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.UUID;
import java.util.function.BiConsumer;

public class EmergencyProtectorEnchantment extends AttributedGunEnchantmentBase {
    public static final UUID MODIFIER_ID = UUID.fromString("c9d85a00-6a8c-43f0-866d-c52a329d422f");

    public static AttributeModifier createModifier(double amount) {
        return new AttributeModifier(MODIFIER_ID, "Emergency Protector Enchantment", amount, AttributeModifier.Operation.ADDITION);
    }

    public EmergencyProtectorEnchantment() {
        this(Rarity.COMMON, ModContent.Enchantments.THE_CATEGORY, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    }

    public EmergencyProtectorEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    @Override
    public int getMinCost(int level) {
        return 5 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        if (level >= getMaxLevel()) {
            return 50000;
        } else {
            return this.getMinCost(level) + 50;
        }
    }

    @Override
    public int getMaxLevel() {
        return Apotheosis.enableEnch ? 6 : 5;
    }

    public double getArmorBonus(int level) {
        return level;
    }

    @Override
    public void addAttributes(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> addModifierMethod, EquipmentSlot slot) {
        if (slot != EquipmentSlot.OFFHAND) {
            return;
        }
        var modifier = createModifier(getArmorBonus(level));
        addModifierMethod.accept(Attributes.ARMOR, modifier);
    }
}
