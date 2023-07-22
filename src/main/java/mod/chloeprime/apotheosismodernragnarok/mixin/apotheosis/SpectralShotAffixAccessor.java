package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import shadows.apotheosis.adventure.affix.effect.SpectralShotAffix;
import shadows.apotheosis.adventure.loot.LootRarity;

@Mixin(value = SpectralShotAffix.class, remap = false)
public interface SpectralShotAffixAccessor {
    @Invoker
    float invokeGetTrueLevel(LootRarity rarity, float level);
}
