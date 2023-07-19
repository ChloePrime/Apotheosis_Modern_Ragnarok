package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.apotheosis.adventure.affix.effect.PotionAffix;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

import java.util.Map;
import java.util.Set;

@Mixin(value = PotionAffix.class, remap = false)
public class MixinPotionAffix {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void inject_defaultAllowGuns(
            Map<LootRarity, PotionAffix.EffectInst> effects,
            Set<LootCategory> types,
            PotionAffix.Target target,
            int cooldown,
            CallbackInfo ci
    ) {
        if (types.contains(LootCategory.BOW) || types.contains(LootCategory.CROSSBOW)) {
            types.add(ModContent.LootCategories.GUN);
        }
    }
}
