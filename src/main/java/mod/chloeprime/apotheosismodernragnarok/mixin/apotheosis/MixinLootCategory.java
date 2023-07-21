package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import mod.chloeprime.apotheosismodernragnarok.common.internal.LootCategoryExtensions;
import mod.chloeprime.apotheosismodernragnarok.common.internal.LootCategoryFactory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(value = LootCategory.class, remap = false)
public class MixinLootCategory implements LootCategoryFactory {
    @Shadow(remap = false) @Final @Mutable private static LootCategory[] $VALUES;
    @Shadow @Final @Mutable static LootCategory[] VALUES;

    // Enum Creator

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inject_onClassInit(CallbackInfo ci) {
        LootCategoryExtensions.init();
    }

    @Override
    public LootCategory apotheosis_modern_ragnarok$create(String name, Predicate<ItemStack> validator, Function<ItemStack, EquipmentSlot[]> slotGetter) {
        var values = new LootCategory[$VALUES.length + 1];
        System.arraycopy($VALUES, 0, values, 0, $VALUES.length);

        var newValue = factory(name, $VALUES[$VALUES.length - 1].ordinal() + 1, validator, slotGetter);

        values[values.length - 1] = newValue;
        VALUES = $VALUES = values;

        return newValue;
    }

    @Invoker(value = "<init>", remap = false)
    public static LootCategory factory(String name, int index, Predicate<ItemStack> validator, Function<ItemStack, EquipmentSlot[]> slotGetter) {
        throw new AssertionError();
    }

    // Others

    @Unique
    private Boolean apotheosis_modern_ragnarok$isRanged;

    @Override
    public void apotheosis_modern_ragnarok$setRanged(boolean isRanged) {
        this.apotheosis_modern_ragnarok$isRanged = isRanged;
    }

    @Inject(method = "isRanged", at = @At("HEAD"), cancellable = true)
    public void overrideRanged(CallbackInfoReturnable<Boolean> cir) {
        if (apotheosis_modern_ragnarok$isRanged != null) {
            cir.setReturnValue(apotheosis_modern_ragnarok$isRanged);
        }
    }
}
