package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import com.google.gson.Gson;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.internal.DynamicEnumTypeAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadows.apotheosis.Apotheosis;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.json.PlaceboJsonReloadListener;

@Mixin(value = Affix.class, remap = false)
public class MixinAffix extends PlaceboJsonReloadListener.TypeKeyedBase<Affix> {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inject_supportLootCategoryExtension(CallbackInfo ci) {
        GSON = GSON.newBuilder().registerTypeAdapter(
                LootCategory.class,
                new DynamicEnumTypeAdapter<>(LootCategory::valueOf, LootCategory::name)
        ).create();
    }

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void changeNameOnGuns(ItemStack stack, LootRarity rarity, float level, boolean prefix, CallbackInfoReturnable<Component> cir) {
        if (AMR$CLEAVING_AFFIX.equals(getId())) {
            var ret = new TranslatableComponent("affix." + this.getId() + AMR$ALT_SYNTAX + (prefix ? "" : ".suffix"));
            cir.setReturnValue(ret);
        }
    }

    @Unique
    private static final ResourceLocation AMR$CLEAVING_AFFIX = Apotheosis.loc("cleaving");

    @Unique
    private static final String AMR$ALT_SYNTAX = "." + ApotheosisModernRagnarok.MOD_ID + ".alt";

    @Shadow @Final @Mutable protected static Gson GSON;
}
