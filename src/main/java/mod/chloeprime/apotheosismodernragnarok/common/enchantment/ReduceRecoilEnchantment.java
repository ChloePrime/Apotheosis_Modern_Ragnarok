package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.google.common.collect.Multimap;
import dev.shadowsoffire.apotheosis.Apotheosis;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.UUID;

public class ReduceRecoilEnchantment extends AttributedGunEnchantmentBase {
    public static final UUID MODIFIER_ID = UUID.fromString("3bcb80de-3506-486e-a1da-4488008a23b7");

    public static AttributeModifier createModifier(double amount) {
        return new AttributeModifier(MODIFIER_ID, "Accuracy Enchantment", amount, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public ReduceRecoilEnchantment() {
        this(Rarity.COMMON, ModContent.Enchantments.THE_CATEGORY, EquipmentSlot.MAINHAND);
    }

    public ReduceRecoilEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
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

    public double getRecoilFactor(int level) {
        return 30.0 / (30 + level);
    }

    @Override
    public void addAttributes(int level, Multimap<Attribute, AttributeModifier> table, EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) {
            return;
        }
        var modifier = createModifier(getRecoilFactor(level) - 1);
        table.put(GunAttributes.H_RECOIL.get(), modifier);
        table.put(GunAttributes.V_RECOIL.get(), modifier);
    }
}
