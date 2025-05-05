package mod.chloeprime.apotheosismodernragnarok.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ApothReforgeFunction extends LootItemConditionalFunction {
    private final ResourceLocation rarity;

    protected ApothReforgeFunction(
            LootItemCondition[] conditions,
            ResourceLocation rarity) {
        super(conditions);
        this.rarity = rarity;
    }

    @Override
    protected @Nonnull ItemStack run(ItemStack stack, LootContext context) {
        var rarity = RarityRegistry.INSTANCE.getValue(this.rarity);
        if (rarity == null) {
            return stack;
        }
        AffixHelper.setAffixes(stack, Collections.emptyMap());
        return LootController.createLootItem(stack, rarity, context.getRandom());
    }

    public static Builder<?> apothReforge(ResourceLocation rarity) {
        return simpleBuilder((conditions) -> new ApothReforgeFunction(conditions, rarity));
    }

    @Override
    public @Nonnull LootItemFunctionType getType() {
        return Objects.requireNonNull(ModContent.LootFunctions.APOTH_REFORGE);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ApothReforgeFunction> {
        public void serialize(JsonObject json, ApothReforgeFunction instance, JsonSerializationContext serializationContext) {
            super.serialize(json, instance, serializationContext);
            json.addProperty("rarity", instance.rarity.toString());
        }

        public @Nonnull ApothReforgeFunction deserialize(JsonObject json, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            ResourceLocation rarity = new ResourceLocation(GsonHelper.getAsString(json, "rarity"));
            return new ApothReforgeFunction(conditions, rarity);
        }
    }
}
