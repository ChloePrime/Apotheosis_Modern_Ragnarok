package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = Gem.class, remap = false)
public interface GemAccessor {
    @Mutable @Accessor void setBonuses(List<GemBonus> value);
    @Mutable @Accessor void setUuidsNeeded(int value);
    @Mutable @Accessor void setBonusMap(Map<LootCategory, GemBonus> value);
}
