package mod.chloeprime.apotheosismodernragnarok.api;

import com.tac.guns.interfaces.IProjectileFactory;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface MagicProjectileFactory extends IProjectileFactory {
    Optional<SoundEvent> getShootSound(ItemStack weapon);
}
