package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import com.google.gson.Gson;
import mod.chloeprime.apotheosismodernragnarok.common.internal.DynamicEnumTypeAdapter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.loot.LootCategory;

@Mixin(value = Affix.class, remap = false)
public class MixinAffix {
    @Shadow @Final @Mutable
    protected static Gson GSON;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inject_supportLootCategoryExtension(CallbackInfo ci) {
        GSON = GSON.newBuilder().registerTypeAdapter(
                LootCategory.class,
                new DynamicEnumTypeAdapter<>(LootCategory::valueOf, LootCategory::name)
        ).create();
    }
}
