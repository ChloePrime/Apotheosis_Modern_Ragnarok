package mod.chloeprime.apotheosismodernragnarok.mixin.tacz.gunpack;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import mod.chloeprime.apotheosismodernragnarok.common.gunpack.GunApothData;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGunData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(GunData.class)
public class MixinGunData implements EnhancedGunData {
    @Override
    public Optional<GunApothData> amr$getApothData() {
        return Optional.ofNullable(apotheosis_modern_ragnarok$data);
    }

    @SuppressWarnings({"unused"})
    @SerializedName("apotheosis_modern_ragnarok")
    private @Unique @Nullable GunApothData apotheosis_modern_ragnarok$data;
}
