package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.network.message.ServerMessageSound;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ServerMessageSound.class, remap = false)
public interface ServerMessageSoundAccessor {
    @Accessor
    void setGunId(ResourceLocation id);
}
