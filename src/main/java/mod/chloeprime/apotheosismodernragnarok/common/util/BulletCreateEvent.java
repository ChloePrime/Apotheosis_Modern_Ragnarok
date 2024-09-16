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

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public final class BulletCreateEvent extends Event {
    @ApiStatus.Internal
    public BulletCreateEvent(@Nonnull Projectile bullet, @Nonnull LivingEntity shooter, @Nonnull ItemStack gun) {
        this.bullet = bullet;
        this.shooter = shooter;
        this.gun = gun;
    }

    public @Nonnull Projectile getBullet() {
        return bullet;
    }

    public @Nonnull LivingEntity getShooter() {
        return shooter;
    }

    public @Nonnull ItemStack getGun() {
        return gun;
    }

    private final @Nonnull Projectile bullet;
    private final @Nonnull LivingEntity shooter;
    private final @Nonnull ItemStack gun;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBulletCreate(EntityJoinLevelEvent event) {
        onBulletCreate0(event, false);
    }

    @ApiStatus.Internal
    public static void onBulletCreate0(EntityJoinLevelEvent event, boolean isClientFixCall) {
        if (!isClientFixCall && event.getEntity().level().isClientSide) {
            return;
        }
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
