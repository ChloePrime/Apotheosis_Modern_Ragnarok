package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.mojang.serialization.Codec;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ExtraCodecs {

    public static final Codec<Map<LootRarity, Double>> COEFFICIENT_BY_RARITY = Codec.unboundedMap(
            LootRarity.CODEC,
            Codec.doubleRange(0, Double.MAX_VALUE));

    public static final Codec<Map<LootCategory, Double>> COEFFICIENT_BY_CATEGORY = Codec.unboundedMap(
            LootCategory.CODEC,
            Codec.doubleRange(0, Double.MAX_VALUE));

    public static final Codec<Set<LootCategory>> LOOT_CATEGORY_SET = setOf(LootCategory.CODEC);

    public static <T> Codec<Set<T>> setOf(Codec<T> elementCodec) {
        return Codec.list(elementCodec).xmap(LinkedHashSet::new, ArrayList::new);
    }
}
