package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = Gem.class, remap = false)
public abstract class MixinGem implements EnhancedGem {
    @Unique private long amr$injectorHash = 0;

    @Override
    public long amr_getInjectorHash() {
        return amr$injectorHash;
    }

    @Override
    public void amr_setInjectorHash(long value) {
        amr$injectorHash = value;
    }

    @Mutable @Accessor public abstract void setBonuses(List<GemBonus> value);
    @Mutable @Accessor public abstract void setUuidsNeeded(int value);
    @Mutable @Accessor public abstract void setBonusMap(Map<LootCategory, GemBonus> value);
}
