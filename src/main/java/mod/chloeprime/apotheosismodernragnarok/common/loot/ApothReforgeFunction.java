package mod.chloeprime.apotheosismodernragnarok.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.affix.ItemAffixes;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ApothReforgeFunction extends LootItemConditionalFunction {
    private final DynamicHolder<LootRarity> rarity;

    public static final MapCodec<ApothReforgeFunction> CODEC = RecordCodecBuilder.mapCodec(builder -> commonFields(builder).and(
            RarityRegistry.INSTANCE.holderCodec().fieldOf("rarity").forGetter(func -> func.rarity)
    ).apply(builder, ApothReforgeFunction::new));

    protected ApothReforgeFunction(
            List<LootItemCondition> conditions,
            DynamicHolder<LootRarity> rarity
    ) {
        super(conditions);
        this.rarity = rarity;
    }

    @Override
    protected @Nonnull ItemStack run(ItemStack stack, LootContext context) {
        var rarity = this.rarity.get();
        if (rarity == null) {
            return stack;
        }
        AffixHelper.setAffixes(stack, ItemAffixes.EMPTY);
        var position = BlockPos.containing(Objects.requireNonNullElse(context.getParam(LootContextParams.ORIGIN), Vec3.ZERO));
        var genContext = GenContext.standalone(context.getRandom(), WorldTier.PINNACLE, context.getLuck(), context.getLevel(), position);
        return LootController.createLootItem(stack, rarity, genContext);
    }

    public static Builder<?> apothReforge(ResourceLocation rarity) {
        var rarityObject = Objects.requireNonNull(RarityRegistry.INSTANCE.holder(rarity));
        return simpleBuilder((conditions) -> new ApothReforgeFunction(conditions, rarityObject));
    }

    @Override
    public @Nonnull LootItemFunctionType<ApothReforgeFunction> getType() {
        return Objects.requireNonNull(ModContent.LootFunctions.APOTH_REFORGE.get());
    }
}
