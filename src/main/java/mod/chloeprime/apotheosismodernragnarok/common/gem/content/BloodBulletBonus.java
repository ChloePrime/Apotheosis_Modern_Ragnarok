package mod.chloeprime.apotheosismodernragnarok.common.gem.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.GunFireEvent;
import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import it.unimi.dsi.fastutil.objects.*;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AffixBaseUtility;
import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GunGemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.internal.BloodBulletUser;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Map;

public class BloodBulletBonus extends GemBonus implements GunGemBonus {
    public static final ResourceLocation ID = ApotheosisModernRagnarok.loc("blood_bullet");

    public static final Codec<BloodBulletBonus> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    gemClass(),
                    Purity.mapCodec(Codec.FLOAT).fieldOf("hp_cost").forGetter(instance -> instance.hpCost),
                    Purity.mapCodec(Codec.FLOAT).fieldOf("min_damage_ratio").forGetter(instance -> instance.power))
            .apply(inst, BloodBulletBonus::new));

    protected final Object2FloatMap<Purity> hpCost;
    protected final Object2FloatMap<Purity> power;

    public BloodBulletBonus(
            GemClass gemClass,
            Map<Purity, Float> hpCost,
            Map<Purity, Float> power
    ) {
        super(gemClass);
        this.hpCost = new Object2FloatLinkedOpenHashMap<>(hpCost);
        this.power = new Object2FloatLinkedOpenHashMap<>(power);
    }

    @Override
    public boolean supports(Purity rarity) {
        return hpCost.containsKey(rarity) && power.containsKey(rarity);
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        var rarity = gem.purity();
        var hpCost = AffixBaseUtility.fmt(this.hpCost.getFloat(rarity));
        var power = AffixBaseUtility.fmtPercent(this.power.getFloat(rarity));
        return Component
                .translatable("bonus.apotheosis_modern_ragnarok.blood_bullet.desc", hpCost, power)
                .withStyle(AffixBaseUtility.BRIGHT_RED);
    }

    // 效果实现

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void cleanupBeforeGunFire(GunFireEvent event) {
        var shooter = event.getShooter();
        if (shooter == null || shooter.level().isClientSide) {
            return;
        }
        var user = ((BloodBulletUser) shooter);
        // 初始化效力数据
        user.amr$setDefenseIgnoreRatio(0);
    }

    /**
     * 射击时扣血并记录破甲效力。
     */
    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public void onGunFire(ItemStack gun, ItemStack gem, GemInstance instance, GunFireEvent event) {
        var shooter = event.getShooter();
        if (shooter == null || shooter.level().isClientSide) {
            return;
        }
        var user = ((BloodBulletUser) shooter);
        // 初始化效力数据
        user.amr$setDefenseIgnoreRatio(0);

        var power = this.power.getFloat(instance.purity());
        if (power <= 0) {
            return;
        }

        // 创造模式下不扣血
        var hpCost = this.hpCost.getFloat(instance.purity());
        var isCreative = shooter instanceof Player player && player.getAbilities().instabuild;
        if (isCreative) {
            // 创造模式，不需要消耗任何资源，
            // 也就不需要执行任何动作
        } else if (shooter.getAbsorptionAmount() > 0) {
            // 扣黄血。
            // 黄血无论多少，至少能抵扣1发血液弹的消耗。
            shooter.setAbsorptionAmount(shooter.getAbsorptionAmount() - hpCost);
        } else  {
            var maxSafeHealth = Math.max(1, hpCost);
            var currentHealth = shooter.getHealth();
            if (currentHealth <= maxSafeHealth + 1e-4) {
                return;
            }
            var newHealth = Math.max(currentHealth - hpCost, maxSafeHealth);
            // 射手血太少，扣血失败
            if (Math.abs(newHealth - currentHealth) <= 1e-4) {
                return;
            }
            // 扣血
            shooter.setHealth(newHealth);
            // 扣血成功，记录破防数据
        }
        user.amr$setDefenseIgnoreRatio(power);
    }

    /**
     * 效力数据传输：射手 -> 子弹
     */
    @Override
    public void onBulletCreated(ItemStack gun, ItemStack gem, GemInstance instance, BulletCreateEvent event) {
        if (event.getBullet().level().isClientSide) {
            return;
        }
        var power = ((BloodBulletUser) event.getShooter()).amr$getDefenseIgnoreRatio();
        if (power > 0 && event.getBullet() instanceof BloodBulletUser bullet) {
            // 将效力数据从射手 -> 子弹
            bullet.amr$setDefenseIgnoreRatio(power);
            event.getBullet().setData(ModContent.SinceMC1211.DataAttachments.IS_BLOODY, true);
        }
    }

    public static boolean clientIsBloodBullet(Projectile bullet) {
        return bullet.getData(ModContent.SinceMC1211.DataAttachments.IS_BLOODY);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }
}
