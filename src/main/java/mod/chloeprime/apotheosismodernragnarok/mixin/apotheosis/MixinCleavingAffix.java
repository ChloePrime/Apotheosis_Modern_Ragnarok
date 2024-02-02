package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunCategories;
import mod.chloeprime.apotheosismodernragnarok.common.internal.LootCategoryExtensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadows.apotheosis.adventure.affix.effect.CleavingAffix;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;

import java.util.Map;

@Mixin(value = CleavingAffix.class, remap = false)
public class MixinCleavingAffix {
    @Inject(method = "canApplyTo", at = @At("HEAD"), cancellable = true)
    private void makeTaggedGunsCanHaveThisAffix(ItemStack stack, LootRarity rarity, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(GunCategories.Tags.ENABLE_CLEAVING)) {
            cir.setReturnValue(this.values.containsKey(rarity));
        }
    }

    @Inject(method = "doPostAttack", at = @At("HEAD"), cancellable = true)
    private void dontDoOriginalLogicOnGuns(ItemStack stack, LootRarity rarity, float level, LivingEntity user, Entity target, CallbackInfo ci) {
        if (!user.level.isClientSide && LootCategory.forItem(stack) == LootCategoryExtensions.GUN) {
            ci.cancel();
        }
    }

    @Shadow @Final protected Map<LootRarity, ?> values;
}
