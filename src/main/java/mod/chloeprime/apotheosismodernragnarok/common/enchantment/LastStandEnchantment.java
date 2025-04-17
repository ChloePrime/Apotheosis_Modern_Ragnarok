package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.UUID;
import java.util.function.BiConsumer;

public class LastStandEnchantment extends AttributedGunEnchantmentBase {
    public static final UUID MODIFIER_ID = UUID.fromString("09d5524f-8b14-4b73-8ea8-f00cde4613ba");

    public static AttributeModifier createModifier(double amount) {
        return new AttributeModifier(MODIFIER_ID, "Last Stand Enchantment", amount, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public LastStandEnchantment() {
        this(Rarity.RARE, ModContent.Enchantments.THE_CATEGORY, EquipmentSlot.MAINHAND);
    }

    public LastStandEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    public int getMinCost(int level) {
        return 5 + 20 * (level - 1);
    }

    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    public int getMaxLevel() {
        return 2;
    }

    public double getDamageBoost(int level) {
        return level * 0.1;
    }

    @Override
    public void addAttributes(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> addModifierMethod, EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) {
            return;
        }
        var gun = Gunsmith.getGunInfo(stack).orElse(null);
        if (gun == null || gun.getTotalAmmo() > 0) {
            return;
        }
        var modifier = createModifier(getDamageBoost(level));
        addModifierMethod.accept(Attributes.ATTACK_DAMAGE, modifier);
    }
}
