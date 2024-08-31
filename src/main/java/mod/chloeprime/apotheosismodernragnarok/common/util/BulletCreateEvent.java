package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.tacz.guns.api.item.IGun;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod.EventBusSubscriber
public final class BulletCreateEvent extends Event {
    public BulletCreateEvent(Projectile bullet, LivingEntity shooter, ItemStack gun) {
        this.bullet = bullet;
        this.shooter = shooter;
        this.gun = gun;
    }

    public Projectile getBullet() {
        return bullet;
    }

    public LivingEntity getShooter() {
        return shooter;
    }

    public ItemStack getGun() {
        return gun;
    }

    private final Projectile bullet;
    private final LivingEntity shooter;
    private final ItemStack gun;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBulletCreate(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Projectile bullet)) {
            return;
        }
        if (!(bullet.getOwner() instanceof LivingEntity shooter)) {
            return;
        }

        var gun = shooter.getMainHandItem();
        if (IGun.getIGunOrNull(gun) == null) {
            return;
        }

        MinecraftForge.EVENT_BUS.post(new BulletCreateEvent(bullet, shooter, gun));
    }
}
