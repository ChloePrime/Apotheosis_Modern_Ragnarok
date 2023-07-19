package mod.chloeprime.apotheosismodernragnarok.common.affix.category;

import mod.chloeprime.apotheosismodernragnarok.common.internal.LootCategoryFactory;
import shadows.apotheosis.adventure.loot.LootCategory;

public final class LootCategoryBuilder {
    private final LootCategory instance;
    private final LootCategoryFactory mutableInstance;

    public LootCategoryBuilder(LootCategory instance) {
        this.instance = instance;
        this.mutableInstance = (LootCategoryFactory) (Object) instance;
    }

    public LootCategoryBuilder isRanged() {
        this.mutableInstance.apotheosis_modern_ragnarok$setRanged(true);
        return this;
    }

    public LootCategory create() {
        return instance;
    }
}
