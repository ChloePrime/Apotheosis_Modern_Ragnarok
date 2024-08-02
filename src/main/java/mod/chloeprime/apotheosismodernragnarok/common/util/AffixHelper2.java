package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.google.gson.JsonObject;
import mod.chloeprime.apotheosismodernragnarok.common.internal.MemberStealerAffix;
import net.minecraft.util.GsonHelper;

public class AffixHelper2 {

    public static AffixRarityConfigMap readRarityConfig(JsonObject root, String key) {
        JsonObject values = GsonHelper.getAsJsonObject(root, key);
        return MemberStealerAffix.getGson().fromJson(values, AffixRarityConfigMap.class);
    }
}
