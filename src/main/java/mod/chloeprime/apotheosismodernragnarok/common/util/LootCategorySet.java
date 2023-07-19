package mod.chloeprime.apotheosismodernragnarok.common.util;

import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.LinkedHashSet;

public class LootCategorySet extends LinkedHashSet<LootCategory> {
    public LootCategorySet() {
    }

    public LootCategorySet(int capacity) {
        super(capacity);
    }
}
