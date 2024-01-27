package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mod.chloeprime.apotheosismodernragnarok.common.internal.MemberStealerAffix;
import net.minecraft.util.GsonHelper;

public class AffixHelper2 {
    public static AffixValueMap readValues(JsonObject root) {
        JsonObject values = GsonHelper.getAsJsonObject(root, "values");
        return MemberStealerAffix.getGson().fromJson(values, AffixValueMap.class);
    }

    public static AffixRarityConfigMap readRarityConfig(JsonObject root, String key) {
        JsonObject values = GsonHelper.getAsJsonObject(root, key);
        return MemberStealerAffix.getGson().fromJson(values, AffixRarityConfigMap.class);
    }

    public static LootCategorySet readTypes(JsonObject root) {
        JsonArray types = GsonHelper.getAsJsonArray(root, "types");
        return MemberStealerAffix.getGson().fromJson(types, LootCategorySet.class);
    }

    public static ArmorTypeSet readArmorTypes(JsonObject root) {
        JsonArray types = GsonHelper.getAsJsonArray(root, "armor_types", new JsonArray());
        return MemberStealerAffix.getGson().fromJson(types, ArmorTypeSet.class);
    }
}
