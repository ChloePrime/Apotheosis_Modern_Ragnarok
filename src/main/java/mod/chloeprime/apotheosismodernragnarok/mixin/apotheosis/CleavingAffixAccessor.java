package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import shadows.apotheosis.adventure.affix.effect.CleavingAffix;
import shadows.apotheosis.adventure.loot.LootRarity;

@Mixin(value = CleavingAffix.class, remap = false)
public interface CleavingAffixAccessor {
    @Invoker float invokeGetChance(LootRarity rarity, float level);
    @Invoker int invokeGetTargets(LootRarity rarity, float level);
}
