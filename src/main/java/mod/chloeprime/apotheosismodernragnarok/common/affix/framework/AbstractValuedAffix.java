package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class AbstractValuedAffix extends AbstractAffix {
    public AbstractValuedAffix(
            AffixDefinition def,
            Set<LootCategory> categories,
            Map<LootRarity, StepFunction> values) {
        super(def, categories);
        this.values = values;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory category, LootRarity rarity) {
        if (!getValues().containsKey(rarity)) {
            return false;
        }
        return super.canApplyTo(stack, category, rarity);
    }

    public final Map<LootRarity, StepFunction> getValues() {
        return Collections.unmodifiableMap(values);
    }

    public final double getValue(ItemStack gun, AffixInstance instance) {
        return getValue(gun, instance.rarity().get(), instance.level());
    }

    public double getValue(ItemStack gun, LootRarity rarity, float level) {
        return getValues().get(rarity).get(level);
    }

    private final Map<LootRarity, StepFunction> values;
}
