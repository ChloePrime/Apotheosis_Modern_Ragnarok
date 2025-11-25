package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGem;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mixin(value = Gem.class, remap = false)
public abstract class MixinGem implements EnhancedGem {
    @Inject(method = "<init>", at = @At("TAIL"))
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void saveInitialData(int weight, float quality, Set<ResourceLocation> dimensions, Optional<LootRarity> minRarity, Optional<LootRarity> maxRarity, List<GemBonus> bonuses, boolean unique, Optional<Set<String>> stages, CallbackInfo ci) {
        amr$backup$bonuses = bonuses;
        amr$backup$uuidsNeeded = uuidsNeeded;
        amr$backup$bonusMap = bonusMap;
    }

    @Unique private List<GemBonus> amr$backup$bonuses;
    @Unique private int amr$backup$uuidsNeeded;
    @Unique private Map<LootCategory, GemBonus> amr$backup$bonusMap;

    @Override
    public void amr$reset() {
        setBonuses(amr$backup$bonuses);
        setUuidsNeeded(amr$backup$uuidsNeeded);
        setBonusMap(amr$backup$bonusMap);
    }

    @Mutable @Accessor public abstract void setBonuses(List<GemBonus> value);
    @Mutable @Accessor public abstract void setUuidsNeeded(int value);
    @Mutable @Accessor public abstract Map<LootCategory, GemBonus> getBonusMap();
    @Mutable @Accessor public abstract void setBonusMap(Map<LootCategory, GemBonus> value);

    @Shadow @Final protected transient int uuidsNeeded;
    @Shadow @Final protected transient Map<LootCategory, GemBonus> bonusMap;
}
