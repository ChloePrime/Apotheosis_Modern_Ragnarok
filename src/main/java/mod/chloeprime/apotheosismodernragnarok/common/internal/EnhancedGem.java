package mod.chloeprime.apotheosismodernragnarok.common.internal;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;

import java.util.List;
import java.util.Map;

public interface EnhancedGem {
    void amr$reset();
    void setBonuses(List<GemBonus> value);
    void setUuidsNeeded(int value);
    void setBonusMap(Map<LootCategory, GemBonus> value);
}
