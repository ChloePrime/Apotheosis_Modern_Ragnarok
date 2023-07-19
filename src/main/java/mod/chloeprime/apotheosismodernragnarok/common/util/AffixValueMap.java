package mod.chloeprime.apotheosismodernragnarok.common.util;

import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

import java.util.LinkedHashMap;

/**
 * @see AffixHelper2#readValues
 */
public class AffixValueMap extends LinkedHashMap<LootRarity, StepFunction> {
    @SuppressWarnings("unused")
    public AffixValueMap() {
    }

    public AffixValueMap(int initialCapacity) {
        super(initialCapacity);
    }
}
