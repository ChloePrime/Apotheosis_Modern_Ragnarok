package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.PotionAffix;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.TelepathicAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.GunAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

public class AffixAdaptableMixin {
    @Mixin(value = TelepathicAffix.class,remap = false)
    public static class Telepathic {
        @ModifyExpressionValue(method = { "canApplyTo" }, at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apotheosis/adventure/loot/LootCategory;isRanged()Z"))
        private boolean makeApplicableToGuns(boolean original, ItemStack stack, LootCategory cat, LootRarity rarity) {
            return original || ExtraLootCategories.isGun(cat);
        }

        @ModifyExpressionValue(method = { "getDescription" }, at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apotheosis/adventure/loot/LootCategory;isRanged()Z"))
        private boolean fixTooltipCategory(boolean original, ItemStack stack, LootRarity rarity, float level) {
            var cat = LootCategory.forItem(stack);
            return original || ExtraLootCategories.isGun(cat);
        }
    }

    @Mixin(value = PotionAffix.class, remap = false)
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public static abstract class Potion implements GunAffix {
        @Unique @Override
        public void onGunshotPost(ItemStack gun, AffixInstance instance, EntityHurtByGunEvent.Post event) {
            if (this.target == PotionAffix.Target.ARROW_SELF) {
                Optional.ofNullable(event.getAttacker()).ifPresent(owner -> this.applyEffect(owner, instance.rarity().get(), instance.level()));
            }
            else if (this.target == PotionAffix.Target.ARROW_TARGET) {
                if (event.getHurtEntity() instanceof LivingEntity victim) {
                    this.applyEffect(victim, instance.rarity().get(), instance.level());
                }
            }
        }

        @Unique @Override
        public void onGunshotKill(ItemStack gun, AffixInstance instance, EntityKillByGunEvent event) {
            if (this.target == PotionAffix.Target.ARROW_SELF) {
                Optional.ofNullable(event.getAttacker()).ifPresent(owner -> this.applyEffect(owner, instance.rarity().get(), instance.level()));
            }
        }

        @Shadow @Final protected PotionAffix.Target target;
        @Shadow protected abstract void applyEffect(LivingEntity target, LootRarity rarity, float level);
    }
}
