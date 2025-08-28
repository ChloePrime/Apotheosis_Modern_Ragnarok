package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import mod.chloeprime.apotheosismodernragnarok.common.ModContent.SinceMC1211.DataAttachments;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent.SinceMC1211.EnchantmentEffectComponents;
import mod.chloeprime.apotheosismodernragnarok.common.util.MC121Utils;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class BulletRiptideEnchantment {
    public static double modifyFriction(ItemStack stack, RandomSource random, double friction) {
        return Math.max(0, MC121Utils.evaluateEnchantValue(
                EnchantmentEffectComponents.BULLET_UNDERWATER_FRICTION.get(),
                stack, random, (float) friction
        ));
    }

    @SubscribeEvent
    public static void onBulletCreate(BulletCreateEvent event) {
        Projectile bullet = event.getBullet();
        double oldFriction = bullet.getData(DataAttachments.BULLET_UNDERWATER_FRICTION_FACTOR);
        double newFriction = modifyFriction(event.getGun(), event.getShooter().getRandom(), oldFriction);
        // 只有水下阻力在有变化的时候再 set data，以减少网络资源占用
        if (Math.abs(newFriction - oldFriction) > 1e-3) {
            bullet.setData(DataAttachments.BULLET_UNDERWATER_FRICTION_FACTOR, newFriction);
        }
    }

    private BulletRiptideEnchantment() {
    }
}