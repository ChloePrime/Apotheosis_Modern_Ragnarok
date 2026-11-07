package mod.chloeprime.apotheosismodernragnarok.mixin.tacz.client;

import com.tacz.guns.client.resource.pojo.display.SoundData;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = SoundData.class, remap = false)
public interface SoundDataAccessor {
    @Invoker("<init>") static SoundData create(ResourceLocation sound, @Nullable Float volume, @Nullable Float pitch, float volumeRandomRange, float pitchRandomRange) {
        throw new AbstractMethodError();
    }
}
