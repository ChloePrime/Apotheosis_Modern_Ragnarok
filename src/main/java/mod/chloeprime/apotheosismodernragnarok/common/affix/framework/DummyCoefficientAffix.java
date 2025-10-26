package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class DummyCoefficientAffix extends DummyValuedAffix {
    public static final Supplier<Codec<DummyCoefficientAffix>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> inst
            .group(
                    ExtraCodecs.AFFIX_TYPE.fieldOf("affix_type").forGetter(Affix::getType),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    GemBonus.VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues),
                    ExtraCodecs.COEFFICIENT_BY_CATEGORY.fieldOf("coefficients").forGetter(a -> a.coefficients))
            .apply(inst, DummyCoefficientAffix::new)));

    public double getCoefficient(ItemStack gun, LootCategory category) {
        return coefficients.getOrDefault(category, 1.0);
    }

    public double getScaledValue(ItemStack gun, LootCategory category, LootRarity rarity, float level) {
        return getValue(gun, rarity, level) * getCoefficient(gun, category);
    }

    @Override
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        var category = LootCategory.forItem(stack);
        var percent = getScaledValue(stack, category, rarity, level);
        return Component.translatable(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(ItemStack stack, LootRarity rarity, float level) {
        var category = LootCategory.forItem(stack);
        var rate = getScaledValue(stack, category, rarity, level);
        var min = getScaledValue(stack, category, rarity, 0);
        var max = getScaledValue(stack, category, rarity, 1);
        return Component.translatable(desc(), fmtAugmenting(rate, min, max)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    private final Map<LootCategory, Double> coefficients;

    public DummyCoefficientAffix(AffixType type, Set<LootCategory> categories, Map<LootRarity, StepFunction> values, Map<LootCategory, Double> coefficients) {
        super(type, categories, values);
        this.coefficients = coefficients;
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC.get();
    }
}
