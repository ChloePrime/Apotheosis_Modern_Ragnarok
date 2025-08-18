package mod.chloeprime.apotheosismodernragnarok.common.gem.content;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.GunFireEvent;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import it.unimi.dsi.fastutil.objects.*;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AffixBaseUtility;
import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GunGemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.internal.BloodBulletUser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.DamageInfo;
import mod.chloeprime.apotheosismodernragnarok.network.ModNetwork;
import mod.chloeprime.apotheosismodernragnarok.network.S2CMarkBulletAsBloody;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class BloodBulletBonus extends GemBonus implements GunGemBonus {
    public static final ResourceLocation ID = ApotheosisModernRagnarok.loc("blood_bullet");

    public static final Codec<BloodBulletBonus> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    gemClass(),
                    LootRarity.mapCodec(Codec.FLOAT).fieldOf("hp_cost").forGetter(instance -> instance.hpCost),
                    LootRarity.mapCodec(Codec.FLOAT).fieldOf("min_damage_ratio").forGetter(instance -> instance.power))
            .apply(inst, BloodBulletBonus::new));

    protected final Object2FloatMap<LootRarity> hpCost;
    protected final Object2FloatMap<LootRarity> power;

    public BloodBulletBonus(
            GemClass gemClass,
            Map<LootRarity, Float> hpCost,
            Map<LootRarity, Float> power
    ) {
        super(ID, gemClass);
        this.hpCost = new Object2FloatLinkedOpenHashMap<>(hpCost);
        this.power = new Object2FloatLinkedOpenHashMap<>(power);
    }

    @Override
    public GemBonus validate() {
        Preconditions.checkNotNull(hpCost, "Null hp cost table");
        Preconditions.checkNotNull(power, "Null power cost table");
        return this;
    }

    @Override
    public boolean supports(LootRarity rarity) {
        return hpCost.containsKey(rarity) && power.containsKey(rarity);
    }

    @Override
    public int getNumberOfUUIDs() {
        return 0;
    }

    @Override
    public Component getSocketBonusTooltip(ItemStack gem, LootRarity rarity) {
        var hpCost = AffixBaseUtility.fmt(this.hpCost.getFloat(rarity));
        var power = AffixBaseUtility.fmtPercent(this.power.getFloat(rarity));
        return Component
                .translatable("bonus.apotheosis_modern_ragnarok.blood_bullet.desc", hpCost, power)
                .withStyle(AffixBaseUtility.BRIGHT_RED);
    }

    // 效果实现

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

        var power = this.power.getFloat(instance.rarity().get());
        if (power <= 0) {
            return;
        }

        // 创造模式下不扣血
        var hpCost = this.hpCost.getFloat(instance.rarity().get());
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
            ModNetwork.sendToNearby(new S2CMarkBulletAsBloody(event.getBullet().getId()), event.getShooter());
        }
    }

    /**
     * 保证 onLivingAttack 必定不会被取消，
     * 并传输效力数据：子弹 -> DamageSource。
     */
    public static boolean onLivingAttack(DamageSource source, float amount, BooleanSupplier original) {
        var info = ((DamageInfo) source);
        info.amr$setOriginalDamage(amount);
        if (amount <= 0) {
            return original.getAsBoolean();
        }

        var failed = !original.getAsBoolean();
        if (failed) {
            info.amr$setAttackFailed(true);
        }

        Entity bullet = source.getDirectEntity();
        if (bullet instanceof Projectile && bullet instanceof BloodBulletUser bloody && bloody.amr$getDefenseIgnoreRatio() > 0) {
            info.amr$setDefenseIgnoreRatio(bloody.amr$getDefenseIgnoreRatio());
            return true;
        } else {
            return !failed;
        }
    }

    /**
     * 保证不会因为 LivingHurtEvent 被取消而失败
     */
    public static float onLivingHurt(DamageSource src, float amount, Supplier<Float> original) {
        var info = (DamageInfo) src;
        if (info.amr$getOriginalDamage() < amount) {
            info.amr$setOriginalDamage(amount);
        }
        var power = info.amr$getDefenseIgnoreRatio();
        var originalDamage = info.amr$getOriginalDamage();
        var originalValue = (info.amr$isAttackFailed() ? 0 : 1) * original.get();

        if (originalDamage <= 0 || power <= 0) {
            return originalValue;
        } else if (originalValue > originalDamage) {
            // 这一阶段伤害不减反增的情况
            info.amr$setOriginalDamage(originalValue);
            return originalValue;
        } else {
            var ensure = Math.min(1e-4F, originalDamage * power);
            return Math.max(ensure, originalValue);
        }
    }

    /**
     * 确保最小伤害
     */
    public static float onLivingDamage(DamageSource src, float amount, Supplier<Float> original) {
        var info = (DamageInfo) src;
        if (info.amr$getOriginalDamage() < amount) {
            info.amr$setOriginalDamage(amount);
        }
        var power = info.amr$getDefenseIgnoreRatio();
        var originalDamage = info.amr$getOriginalDamage();
        var originalValue = (info.amr$isAttackFailed() ? 0 : 1) * original.get();

        // 第一部分针对伤害大于最低伤害的情况
        if (originalValue >= originalDamage || originalDamage <= 0 || power <= 0) {
            return originalValue;
        } else {
            return Mth.lerp(power, originalValue, originalDamage);
        }
    }

    public static final String PDK_CLIENT_IS_BLOOD_BULLET = ApotheosisModernRagnarok.loc("is_blood_bullet").toString();

    public static boolean clientIsBloodBullet(Projectile bullet) {
        return bullet.getPersistentData().getBoolean(PDK_CLIENT_IS_BLOOD_BULLET);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }
}
