package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractGunItem.class, remap = false)
public abstract class MixinAbstractGunItem extends Item implements IGun {
    @Inject(method = "dropAllAmmo", at = @At("HEAD"), cancellable = true)
    private void dontDropAmmoForProjectionMagicGuns(Player player, ItemStack gunItem, CallbackInfo ci) {
        if (gunItem.getEnchantmentLevel(ModContent.Enchantments.PROJECTION_MAGIC.get()) > 0) {
            setCurrentAmmoCount(gunItem, 0);
            ci.cancel();
        }
    }

    public MixinAbstractGunItem(Properties pProperties) {
        super(pProperties);
    }
}
