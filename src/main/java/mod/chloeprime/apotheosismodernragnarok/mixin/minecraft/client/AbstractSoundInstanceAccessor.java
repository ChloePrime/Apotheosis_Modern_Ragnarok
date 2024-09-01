package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSoundInstance.class)
public interface AbstractSoundInstanceAccessor {
    @Accessor("volume") float amr$getVolume();
    @Accessor("pitch") float amr$getPitch();
}
