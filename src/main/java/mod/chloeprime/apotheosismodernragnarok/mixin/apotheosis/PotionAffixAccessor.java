package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import shadows.apotheosis.adventure.affix.effect.PotionAffix;
import shadows.apotheosis.adventure.loot.LootRarity;

import java.util.Map;

@Mixin(value = PotionAffix.class, remap = false)
public interface PotionAffixAccessor {
    @Accessor
    Map<LootRarity, PotionAffix.EffectInst> getEffects();
    @Accessor
    PotionAffix.Target getTarget();
    @Invoker
    void invokeApplyEffect(LivingEntity target, PotionAffix.EffectInst inst, float level);
}
