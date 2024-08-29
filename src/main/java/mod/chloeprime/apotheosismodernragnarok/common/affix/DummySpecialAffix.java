package mod.chloeprime.apotheosismodernragnarok.common.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class DummySpecialAffix extends AbstractAffix {
    public static final Codec<DummySpecialAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ExtraCodecs.AFFIX_TYPE.fieldOf("affix_type").forGetter(Affix::getType),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    LootRarity.CODEC.fieldOf("min_rarity").forGetter(a -> a.minRarity))
            .apply(inst, DummySpecialAffix::new));

    protected LootRarity minRarity;

    public DummySpecialAffix(AffixType type, Set<LootCategory> categories, LootRarity minRarity) {
        super(type, categories);
        this.minRarity = minRarity;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory category, LootRarity rarity) {
        return super.canApplyTo(stack, category, rarity) && rarity.isAtLeast(this.minRarity);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
