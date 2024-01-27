package mod.chloeprime.apotheosismodernragnarok.common.util;

import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import shadows.apotheosis.adventure.loot.LootRarity;

public class AffixRarityConfigMap extends Object2FloatLinkedOpenHashMap<LootRarity> {
    @SuppressWarnings("unused")
    public AffixRarityConfigMap() {
        super();
    }

    public AffixRarityConfigMap(int capacity) {
        super(capacity);
    }
}
