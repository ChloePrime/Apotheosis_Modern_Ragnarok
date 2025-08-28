package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Map;
import java.util.Set;

public class DummyValuedAffix extends AbstractValuedAffix {
    public static final Codec<DummyValuedAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    affixDef(),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    LootRarity.mapCodec(StepFunction.CODEC).fieldOf("values").forGetter(AbstractValuedAffix::getValues))
            .apply(inst, DummyValuedAffix::new));

    public DummyValuedAffix(AffixDefinition def, Set<LootCategory> categories, Map<LootRarity, StepFunction> values) {
        super(def, categories, values);
    }

    @Override
    public MutableComponent getDescription(AffixInstance inst, AttributeTooltipContext ctx) {
        var stack = inst.stack();
        var rarity = inst.getRarity();
        float level = inst.level();
        var percent = getValue(stack, rarity, level);
        return Component.translatable(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(AffixInstance inst, AttributeTooltipContext ctx) {
        var stack = inst.stack();
        var rarity = inst.getRarity();
        float level = inst.level();
        var rate = getValue(stack, rarity, level);
        var min = getValue(stack, rarity, 0);
        var max = getValue(stack, rarity, 1);
        return Component.translatable(desc(), fmtAugmenting(rate, min, max)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
