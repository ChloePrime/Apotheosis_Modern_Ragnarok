package mod.chloeprime.apotheosismodernragnarok.common.affix;

import com.google.gson.JsonObject;
import mod.chloeprime.apotheosismodernragnarok.common.util.AffixHelper2;
import mod.chloeprime.apotheosismodernragnarok.common.util.AffixValueMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.AffixInstance;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

public abstract class AbstractValuedAffix extends AbstractAffix {
    public AbstractValuedAffix(AffixType type, Pojo data) {
        super(type, data);
        this.values = data.values;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
        if (!getValues().containsKey(rarity)) {
            return false;
        }
        return super.canApplyTo(stack, rarity);
    }

    public final AffixValueMap getValues() {
        return values;
    }

    public final float getValue(ItemStack gun, AffixInstance instance) {
        return getValue(gun, instance.rarity(),instance.level());
    }

    public float getValue(ItemStack gun, LootRarity rarity, float level) {
        return getValues().get(rarity).get(level);
    }

    private final AffixValueMap values;

    /**
     * super <br>
     * values
     */
    public static void readBase(JsonObject obj, Pojo dataHolder) {
        AbstractAffix.readBase(obj, dataHolder);
        // values
        dataHolder.values = AffixHelper2.readValues(obj);
    }

    /**
     * super <br>
     * values
     */
    public static void readBase(FriendlyByteBuf buf, Pojo dataHolder) {
        AbstractAffix.readBase(buf, dataHolder);
        // values
        dataHolder.values = buf.readMap(AffixValueMap::new, b -> LootRarity.byId(b.readUtf()), StepFunction::read);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        // values
        buf.writeMap(getValues(), (b, rarity) -> b.writeUtf(rarity.id()), (b, stepFunction) -> stepFunction.write(b));
    }

    public static class Pojo extends AbstractAffix.Pojo {
        public AffixValueMap values;
    }
}
