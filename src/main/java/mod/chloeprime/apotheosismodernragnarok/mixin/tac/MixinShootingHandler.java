package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.client.handler.ShootingHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ShootingHandler.class, remap = false, priority = 50)
public class MixinShootingHandler {
    /**
     * @author ChloePrime
     * @reason remove ammo limit thoroughly
     */
    @Overwrite
    private boolean magError(Player player, ItemStack heldItem) {
        return false;
    }
}
