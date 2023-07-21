package mod.chloeprime.apotheosismodernragnarok.common.entity;

import com.tac.guns.common.Gun;
import com.tac.guns.item.GunItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ProjectileBuilder {
    public ProjectileBuilder shooter(LivingEntity shooter) {
        this.shooter = shooter;
        return this;
    }

    public ProjectileBuilder weapon(ItemStack weapon) {
        this.weapon = weapon;
        return this;
    }

    public ProjectileBuilder item(GunItem item) {
        this.gunItem = item;
        return this;
    }

    public ProjectileBuilder data(Gun data) {
        this.data = data;
        return this;
    }

    public ProjectileBuilder recoil(float pitch, float yaw) {
        rrp = pitch;
        rry = yaw;
        return this;
    }

    LivingEntity shooter;
    ItemStack weapon;
    GunItem gunItem;
    Gun data;
    float rrp, rry;
}
