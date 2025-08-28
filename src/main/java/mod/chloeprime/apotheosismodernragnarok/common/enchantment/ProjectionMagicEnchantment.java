package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.util.AttachmentDataUtils;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ProjectionMagicUser;
import mod.chloeprime.apotheosismodernragnarok.common.util.MC121Utils;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static com.tacz.guns.api.entity.ReloadState.StateType.*;
import static mod.chloeprime.apotheosismodernragnarok.common.ModContent.SinceMC1211;

@EventBusSubscriber
public class ProjectionMagicEnchantment {

    public static float getDelayFactor(ItemStack stack, RandomSource random) {
        return Math.max(0, MC121Utils.evaluateEnchantValue(
                SinceMC1211.EnchantmentEffectComponents.PROJECTION_MAGIC_DELAY.get(),
                stack, random, 0
        ));
    }

    @SubscribeEvent
    private static void onPlayerTick(PlayerTickEvent.Pre event) {
        var user = event.getEntity();
        if (user.level().isClientSide) {
            return;
        }

        var now = user.level().getGameTime();
        var hash = user.hashCode();
        if ((now + hash) % 2 != 0) {
            return;
        }

        var pmUser = (ProjectionMagicUser) user;
        var weapon = user.getMainHandItem();

        if (IGun.getIGunOrNull(weapon) == null || IGunOperator.fromLivingEntity(user).getSynReloadState().getStateType() != NOT_RELOADING) {
            pmUser.amr$deactivateProjectionMagic();
            return;
        }
        var delayFactor = getDelayFactor(weapon, user.getRandom());
        if (delayFactor <= 0) {
            pmUser.amr$deactivateProjectionMagic();
            return;
        }
        var gun = Gunsmith.getGunInfo(weapon).orElse(null);
        if (gun == null) {
            return;
        }
        if (pmUser.amr$activateProjectionMagic()) {
            updateDelayFor(user, delayFactor);
        }
        if (now >= pmUser.amr$getProjectionMagicStartEta()) {
            reloadGun(gun);
        }
    }

    @SubscribeEvent
    public static void onPlayerShoot(GunShootEvent event) {
        if (event.getShooter().level().isClientSide) {
            return;
        }
        updateDelayFor(event.getShooter(), getDelayFactor(event.getGunItemStack(), event.getShooter().getRandom()));
    }

    public static void updateDelayFor(LivingEntity user, float delayFactor) {
        Gunsmith.getGunInfo(user.getMainHandItem()).ifPresentOrElse(
                gun -> updateDelayFor(user, gun, delayFactor),
                () -> ((ProjectionMagicUser) user).amr$deactivateProjectionMagic());
    }

    public static void updateDelayFor(LivingEntity user, GunInfo gun, float delayFactor) {
        var now = user.level().getGameTime();
        var delay = 2 * 20 * (gun.getTotalAmmo() > 0
                ? gun.index().getGunData().getReloadData().getFeed().getTacticalTime()
                : gun.index().getGunData().getReloadData().getFeed().getEmptyTime());
        ((ProjectionMagicUser) user).amr$setProjectionMagicStartEta(Math.round(now + delay * delayFactor));
    }

    public static void reloadGun(GunInfo gun) {
        if (gun.getTotalAmmo() == gun.getTotalMagazineSize()) {
            return;
        }
        var curAmount = gun.gunItem().getCurrentAmmoCount(gun.gunStack());
        var maxAmount = AttachmentDataUtils.getAmmoCountWithAttachment(gun.gunStack(), gun.index().getGunData());
        var fillAmount = Mth.clamp(maxAmount / 25, 1, CommonConfig.PROJECTION_MAGIC_MAX_FILL_SPEED.get());

        if (gun.index().getGunData().getBolt() != Bolt.OPEN_BOLT) {
            if (!gun.gunItem().hasBulletInBarrel(gun.gunStack())) {
                gun.gunItem().setBulletInBarrel(gun.gunStack(), true);
                fillAmount -= 1;
            }
        }

        var newAmount = Math.min(maxAmount, curAmount + fillAmount);
        gun.gunItem().setCurrentAmmoCount(gun.gunStack(), newAmount);
    }
}
