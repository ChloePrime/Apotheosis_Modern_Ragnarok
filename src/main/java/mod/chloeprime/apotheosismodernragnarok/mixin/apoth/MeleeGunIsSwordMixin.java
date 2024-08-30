package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = LootCategory.class, remap = false)
public class MeleeGunIsSwordMixin {
    @ModifyExpressionValue(
            method = "forItem",
            at = @At(value = "INVOKE", ordinal = 0, target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
            slice = @Slice(
                    from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Ldev/shadowsoffire/apotheosis/adventure/AdventureConfig;TYPE_OVERRIDES:Ljava/util/Map;"),
                    to = @At("TAIL")
            ))
    private static Object overrideLootCategory(Object original, ItemStack item) {
        if (original == null && GunPredicate.isMeleeGun(item)) {
            return LootCategory.SWORD;
        }
        return original;
    }
}
