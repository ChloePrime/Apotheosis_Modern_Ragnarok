package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Map;
import java.util.Set;

public class DummyCoefficientAffix extends DummyValuedAffix {
    public static final Codec<DummyCoefficientAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    affixDef(),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    LootRarity.mapCodec(StepFunction.CODEC).fieldOf("values").forGetter(AbstractValuedAffix::getValues),
                    ExtraCodecs.COEFFICIENT_BY_CATEGORY.fieldOf("coefficients").forGetter(a -> a.coefficients))
            .apply(inst, DummyCoefficientAffix::new));

    public double getCoefficient(ItemStack gun, LootCategory category) {
        return coefficients.getOrDefault(category, 1.0);
    }

    public double getScaledValue(ItemStack gun, LootCategory category, LootRarity rarity, float level) {
        return getValue(gun, rarity, level) * getCoefficient(gun, category);
    }

    @Override
    public MutableComponent getDescription(AffixInstance inst, AttributeTooltipContext ctx) {
        var stack = inst.stack();
        var rarity = inst.getRarity();
        float level = inst.level();
        var category = inst.category();
        var percent = getScaledValue(stack, category, rarity, level);
        return Component.translatable(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(AffixInstance inst, AttributeTooltipContext ctx) {
        var stack = inst.stack();
        var rarity = inst.getRarity();
        float level = inst.level();
        var category = inst.category();
        var rate = getScaledValue(stack, category, rarity, level);
        var min = getScaledValue(stack, category, rarity, 0);
        var max = getScaledValue(stack, category, rarity, 1);
        return Component.translatable(desc(), fmtAugmenting(rate, min, max)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    private final Map<LootCategory, Double> coefficients;

    public DummyCoefficientAffix(AffixDefinition def, Set<LootCategory> categories, Map<LootRarity, StepFunction> values, Map<LootCategory, Double> coefficients) {
        super(def, categories, values);
        this.coefficients = coefficients;
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
