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

public class DummyValuedAffix extends AbstractValuedAffix {

    public static final Supplier<Codec<DummyValuedAffix>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> inst
            .group(
                    ExtraCodecs.AFFIX_TYPE.fieldOf("affix_type").forGetter(Affix::getType),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    GemBonus.VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues))
            .apply(inst, DummyValuedAffix::new)));

    public DummyValuedAffix(AffixType type, Set<LootCategory> categories, Map<LootRarity, StepFunction> values) {
        super(type, categories, values);
    }

    @Override
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        var percent = getValue(stack, rarity, level);
        return Component.translatable(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(ItemStack stack, LootRarity rarity, float level) {
        var rate = getValue(stack, rarity, level);
        var min = getValue(stack, rarity, 0);
        var max = getValue(stack, rarity, 1);
        return Component.translatable(desc(), fmtAugmenting(rate, min, max)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC.get();
    }
}
