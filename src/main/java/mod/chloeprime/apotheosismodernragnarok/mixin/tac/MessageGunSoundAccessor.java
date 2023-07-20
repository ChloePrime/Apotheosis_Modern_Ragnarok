package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.network.message.MessageGunSound;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MessageGunSound.class, remap = false)
public interface MessageGunSoundAccessor {
    @Accessor
    void setId(ResourceLocation id);
}
