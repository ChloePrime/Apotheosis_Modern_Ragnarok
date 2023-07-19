package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.affix.AttributeAffix;
import shadows.apotheosis.adventure.loot.LootCategory;

import java.util.Set;

@Mixin(value = AttributeAffix.class, remap = false)
public abstract class MixinAttributeAffix extends Affix {
    @Unique
    @Override
    public void setId(ResourceLocation id) {
        super.setId(id);
        if (CommonConfig.GUN_COMPATIBLE_AFFIX.get().contains(id.toString())) {
            types.add(ModContent.LootCategories.GUN);
        }
    }

    @Shadow @Final protected Set<LootCategory> types;

    MixinAttributeAffix(AffixType type) {
        super(type);
    }
}
