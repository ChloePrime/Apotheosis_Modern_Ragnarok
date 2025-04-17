package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.util.AttachmentDataUtils;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ProjectionMagicUser;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@MethodsReturnNonnullByDefault
public class ProjectionMagicEnchantment extends Enchantment {
    public ProjectionMagicEnchantment() {
        this(Rarity.VERY_RARE, ModContent.Enchantments.THE_CATEGORY, EquipmentSlot.MAINHAND);
    }

    public ProjectionMagicEnchantment(Rarity pRarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(pRarity, category, slots);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getMinCost(int level) {
        return 55 + level * level * 12; // 57 / 103 / 163
    }

    @Override
    public int getMaxCost(int level) {
        return 50000;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public Component getFullname(int level) {
        return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_GREEN);
    }

    public double getRegenDelayFactor(int level) {
        if (level < 0) {
            return 1;
        }
        return 3.0 / (level + 2) * 2;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        var user = event.player;
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

        if (IGun.getIGunOrNull(weapon) == null) {
            pmUser.amr$deactivateProjectionMagic();
            return;
        }
        int level = weapon.getEnchantmentLevel(this);
        if (level <= 0) {
            pmUser.amr$deactivateProjectionMagic();
            return;
        }
        var gun = Gunsmith.getGunInfo(weapon).orElse(null);
        if (gun == null) {
            return;
        }
        if (pmUser.amr$activateProjectionMagic()) {
            updateDelayFor(user);
        }
        if (now >= pmUser.amr$getProjectionMagicStartEta()) {
            reloadGun(gun);
        }
    }

    @SubscribeEvent
    public void onPlayerShoot(GunShootEvent event) {
        if (event.getShooter().level().isClientSide) {
            return;
        }
        updateDelayFor(event.getShooter());
    }

    public void updateDelayFor(LivingEntity user) {
        Gunsmith.getGunInfo(user.getMainHandItem()).ifPresentOrElse(
                gun -> updateDelayFor(user, gun),
                () -> ((ProjectionMagicUser) user).amr$deactivateProjectionMagic());
    }

    public void updateDelayFor(LivingEntity user, GunInfo gun) {
        var now = user.level().getGameTime();
        var delay = 2 * 20 * (gun.getTotalAmmo() > 0
                ? gun.index().getGunData().getReloadData().getFeed().getTacticalTime()
                : gun.index().getGunData().getReloadData().getFeed().getEmptyTime());
        var delayFactor = getRegenDelayFactor(gun.gunStack().getEnchantmentLevel(this));
        ((ProjectionMagicUser) user).amr$setProjectionMagicStartEta(Math.round(now + delay * delayFactor));
    }

    public void reloadGun(GunInfo gun) {
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
