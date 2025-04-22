package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.index.CommonGunIndex;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGunData;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = LootCategory.class, remap = false)
public class MeleeGunIsSwordMixin {
    @Shadow @Final public static LootCategory HEAVY_WEAPON;
    @Shadow @Final public static LootCategory SWORD;

    @ModifyExpressionValue(
            method = "forItem",
            at = @At(value = "INVOKE", ordinal = 0, target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
            slice = @Slice(
                    from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Ldev/shadowsoffire/apotheosis/adventure/AdventureConfig;TYPE_OVERRIDES:Ljava/util/Map;"),
                    to = @At("TAIL")
            ))
    private static Object overrideLootCategory(Object original, ItemStack item) {
        if (original == null && GunPredicate.isDedicatedTaCZMeleeWeapon(item)) {
            return Gunsmith.getGunInfo(item)
                    .map(GunInfo::index)
                    .map(CommonGunIndex::getGunData)
                    .flatMap(gunData -> ((EnhancedGunData) gunData).amr$getApothData())
                    .map(apoth -> (Object)(apoth.is_heavy_melee_weapon ? HEAVY_WEAPON : SWORD))
                    .orElse(SWORD);
        }
        return original;
    }
}
