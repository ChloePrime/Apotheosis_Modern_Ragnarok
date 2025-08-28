package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Set;

public class DummySpecialAffix extends AbstractAffix {
    public static final Codec<DummySpecialAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    affixDef(),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    PlaceboCodecs.setOf(LootRarity.CODEC).fieldOf("min_rarity").forGetter(a -> a.rarities))
            .apply(inst, DummySpecialAffix::new));

    protected final Set<LootRarity> rarities;

    public DummySpecialAffix(AffixDefinition def, Set<LootCategory> categories, Set<LootRarity> rarities) {
        super(def, categories);
        this.rarities = rarities;
    }

    @Override
    public MutableComponent getDescription(AffixInstance inst, AttributeTooltipContext ctx) {
        return Component.translatable(desc()).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory category, LootRarity rarity) {
        return super.canApplyTo(stack, category, rarity) && rarities.contains(rarity);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
