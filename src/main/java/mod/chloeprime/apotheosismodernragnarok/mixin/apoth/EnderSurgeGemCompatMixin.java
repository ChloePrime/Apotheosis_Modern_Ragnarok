package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GemBonus.class, remap = false)
public class EnderSurgeGemCompatMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addGunsToCategorySet(GemClass gemClass, CallbackInfo ci) {
        if ("anything".equals(gemClass.key()) && !ExtraLootCategories.all().stream().allMatch(gemClass.types()::contains)) {
            var injectedCategories = new OrHolderSet<>(gemClass.types(), ExtraLootCategories.all());
            this.gemClass = new GemClass(gemClass.key(), injectedCategories);
        }
    }

    @Shadow @Final @Mutable
    protected GemClass gemClass;
}
