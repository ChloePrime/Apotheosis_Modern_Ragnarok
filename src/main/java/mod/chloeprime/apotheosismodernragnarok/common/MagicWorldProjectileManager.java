package mod.chloeprime.apotheosismodernragnarok.common;

import com.tac.guns.init.ModItems;
import com.tac.guns.interfaces.IProjectileFactory;
import com.tac.guns.item.GunItem;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicFireball;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicLaser;
import net.minecraft.world.item.ItemStack;

public class MagicWorldProjectileManager {
    public static IProjectileFactory get(ItemStack gun, GunItem gunItem) {
        return ModItems.RPG7_MISSILE.getId().equals(gunItem.getGun().getProjectile().getItem())
                ? MagicFireball.Factory.INSTANCE
                : MagicLaser.Factory.INSTANCE;
    }

    private MagicWorldProjectileManager() {}
}
