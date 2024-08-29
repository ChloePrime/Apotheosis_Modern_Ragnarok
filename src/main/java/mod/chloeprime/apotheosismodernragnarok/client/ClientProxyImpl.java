package mod.chloeprime.apotheosismodernragnarok.client;

import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientProxyImpl {
    @OnlyIn(Dist.CLIENT)
    public static boolean hasLang(String langKey) {
        return I18n.exists(langKey);
    }
}
