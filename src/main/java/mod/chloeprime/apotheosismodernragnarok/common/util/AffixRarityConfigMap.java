package mod.chloeprime.apotheosismodernragnarok.common.util;

import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
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
