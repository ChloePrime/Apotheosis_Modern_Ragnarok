package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mixin(value = GemBonus.class, remap = false)
public class EnderSurgeGemCompatMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addGunsToCategorySet(ResourceLocation id, GemClass gemClass, CallbackInfo ci) {
        if ("anything".equals(gemClass.key()) && !gemClass.types().containsAll(ExtraLootCategories.all())) {
            var injectedCategories = new HashSet<LootCategory>();
            injectedCategories.addAll(gemClass.types());
            injectedCategories.addAll(ExtraLootCategories.all());
            this.gemClass = new GemClass(gemClass.key(), injectedCategories);
        }
    }

    @Shadow @Final @Mutable
    protected GemClass gemClass;
}
