package mod.chloeprime.apotheosismodernragnarok.common.affix;

import com.google.gson.JsonObject;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.BulletSaverAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.AffixHelper2;
import mod.chloeprime.apotheosismodernragnarok.common.util.AffixValueMap;
import mod.chloeprime.apotheosismodernragnarok.common.util.ArmorTypeSet;
import mod.chloeprime.apotheosismodernragnarok.common.util.LootCategorySet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixInstance;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.util.StepFunction;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractValuedAffix extends Affix {
    public AbstractValuedAffix(AffixType type, Pojo data) {
        super(type);
        this.values = data.values;
        this.types = data.types;
        this.armorTypes = data.armorTypes;
    }

    public AffixValueMap getValues() {
        return values;
    }

    public LootCategorySet getTypes() {
        return types;
    }

    public ArmorTypeSet getArmorTypes() {
        return armorTypes;
    }

    public final float getValue(ItemStack gun, AffixInstance instance) {
        return getValue(gun, instance.rarity(),instance.level());
    }

    public float getValue(ItemStack gun, LootRarity rarity, float level) {
        return getValues().get(rarity).get(level);
    }

    public String desc() {
        return "affix." + getId() + ".desc";
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
        if (!values.containsKey(rarity)) {
            return false;
        }
        var category = LootCategory.forItem(stack);
        if (category == LootCategory.NONE) {
            return false;
        }
        var validTypes = getTypes();
        if (!validTypes.isEmpty() && !validTypes.contains(category)) {
            return false;
        }
        var validSlots = getArmorTypes();
        if (category == LootCategory.ARMOR && !validSlots.isEmpty() && !armorTypes.contains(stack.getEquipmentSlot())) {
            return false;
        }
        // finally
        return true;
    }

    private final AffixValueMap values;
    private final LootCategorySet types;
    private final ArmorTypeSet armorTypes;

    /**
     * @see BulletSaverAffix#read(JsonObject)       example usage (for Json deserialize)
     * @see BulletSaverAffix#read(FriendlyByteBuf)  example usage (for network deserialize)
     */
    public static <A, J, D> A read(D data, Function<J, A> factory, Supplier<J> pojoFactory, BiConsumer<D, J> deserializer) {
        var jojo = pojoFactory.get();
        deserializer.accept(data, jojo);
        return factory.apply(jojo);
    }

    public static void readBase(JsonObject obj, Pojo dataHolder) {
        dataHolder.values = AffixHelper2.readValues(obj);
        dataHolder.types = AffixHelper2.readTypes(obj);
        dataHolder.armorTypes = AffixHelper2.readArmorTypes(obj);
    }

    public static void readBase(FriendlyByteBuf buf, Pojo dataHolder) {
        dataHolder.values = buf.readMap(AffixValueMap::new, b -> LootRarity.byId(b.readUtf()), (StepFunction::read));

        int typeCount = buf.readVarInt();
        dataHolder.types = new LootCategorySet(typeCount);
        for (int i = 0; i < typeCount; i++) {
            dataHolder.types.add(buf.readEnum(LootCategory.class));
        }

        int armorTypeCount = buf.readVarInt();
        dataHolder.armorTypes = new ArmorTypeSet(armorTypeCount);
        for (int i = 0; i < armorTypeCount; i++) {
            dataHolder.armorTypes.add(buf.readEnum(EquipmentSlot.class));
        }
    }

    public JsonObject write() {
        return new JsonObject();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeMap(getValues(), (b, rarity) -> b.writeUtf(rarity.id()), (b, stepFunction) -> stepFunction.write(b));

        buf.writeVarInt(getTypes().size());
        getTypes().forEach(buf::writeEnum);

        buf.writeVarInt(getArmorTypes().size());
        getArmorTypes().forEach(buf::writeEnum);
    }

    public static class Pojo {
        public AffixValueMap values;
        public LootCategorySet types;
        public ArmorTypeSet armorTypes;
    }
}
