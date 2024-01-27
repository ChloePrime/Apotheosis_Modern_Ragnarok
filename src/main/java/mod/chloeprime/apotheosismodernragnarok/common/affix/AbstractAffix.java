package mod.chloeprime.apotheosismodernragnarok.common.affix;

import com.google.gson.JsonObject;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.BulletSaverAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.AffixHelper2;
import mod.chloeprime.apotheosismodernragnarok.common.util.ArmorTypeSet;
import mod.chloeprime.apotheosismodernragnarok.common.util.LootCategorySet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.apotheosis.adventure.loot.LootRarity;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractAffix extends Affix {
    protected final LootCategorySet types;
    protected final ArmorTypeSet armorTypes;

    public AbstractAffix(AffixType type, Pojo data) {
        super(type);
        this.types = data.types;
        this.armorTypes = data.armorTypes;
    }

    public LootCategorySet getTypes() {
        return types;
    }

    public ArmorTypeSet getArmorTypes() {
        return armorTypes;
    }

    public String desc() {
        return "affix." + getId() + ".desc";
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
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

    /**
     * @see BulletSaverAffix#read(JsonObject)       example usage (for Json deserialize)
     * @see BulletSaverAffix#read(FriendlyByteBuf)  example usage (for network deserialize)
     */
    public static <A, J, D> A read(D data, Function<J, A> factory, Supplier<J> pojoFactory, BiConsumer<D, J> deserializer) {
        var jojo = pojoFactory.get();
        deserializer.accept(data, jojo);
        return factory.apply(jojo);
    }

    /**
     * types <br>
     * armorTypes
     */
    public static void readBase(JsonObject obj, Pojo dataHolder) {
        dataHolder.types = AffixHelper2.readTypes(obj);
        dataHolder.armorTypes = AffixHelper2.readArmorTypes(obj);
    }

    /**
     * types <br>
     * armorTypes
     */
    public static void readBase(FriendlyByteBuf buf, AbstractValuedAffix.Pojo dataHolder) {
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

    /**
     * types <br>
     * armorTypes
     */
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(getTypes().size());
        getTypes().forEach(buf::writeEnum);

        buf.writeVarInt(getArmorTypes().size());
        getArmorTypes().forEach(buf::writeEnum);
    }

    public static class Pojo {
        public LootCategorySet types;
        public ArmorTypeSet armorTypes;
    }
}
