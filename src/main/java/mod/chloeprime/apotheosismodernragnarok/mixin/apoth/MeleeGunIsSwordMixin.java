package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.compat.lrtactical.LRTacProxy;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = LootCategory.class, remap = false)
public class MeleeGunIsSwordMixin {
    @ModifyExpressionValue(
            method = "forItem",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/core/DefaultedRegistry;getData(Lnet/neoforged/neoforge/registries/datamaps/DataMapType;Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;"),
            slice = @Slice(
                    from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Ldev/shadowsoffire/apotheosis/Apoth$DataMaps;LOOT_CATEGORY_OVERRIDES:Lnet/neoforged/neoforge/registries/datamaps/DataMapType;"),
                    to = @At("TAIL")
            ))
    private static Object overrideLootCategory(Object original, ItemStack item) {
        var lrCompatCategory = LRTacProxy.getLRTacMeleeType(item);
        if (lrCompatCategory.isPresent()) {
            return lrCompatCategory.get();
        }
        if (original == null && GunPredicate.isDedicatedTaCZMeleeWeapon(item)) {
            return Apoth.LootCategories.MELEE_WEAPON;
        }
        return original;
    }
}
