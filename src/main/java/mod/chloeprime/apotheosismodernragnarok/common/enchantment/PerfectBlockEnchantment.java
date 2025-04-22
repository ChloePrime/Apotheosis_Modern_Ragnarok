package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.event.common.GunMeleeEvent;
import dev.shadowsoffire.apotheosis.Apotheosis;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.internal.PerfectBlockEnchantmentUser;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 完美招架
 */
@Mod.EventBusSubscriber
public class PerfectBlockEnchantment extends Enchantment {
    public static final long CANNOT_BLOCK_DELAY_AFTER_HURT = 10;
    private static final boolean IS_DEV = !FMLLoader.isProduction();

    public PerfectBlockEnchantment() {
        this(Rarity.RARE, ModContent.Enchantments.CAT_MELEE_CAPABLE, EquipmentSlot.MAINHAND);
    }

    public PerfectBlockEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getMaxLevel() {
        return Apotheosis.enableEnch
                ? 5
                : 3;
    }

    @Override
    public int getMinCost(int level) {
        return Apotheosis.enableEnch
                ? 15 + level * level * 7 // 22 ~ 190
                : 15 + level * 5; // 20 ~ 30
    }

    @Override
    public int getMaxCost(int pLevel) {
        return 50000;
    }

    private long getDefenseDurationTicks(int enchantLevel) {
        // 开发环境下判定时间+1秒，方便测试
        return 1 + enchantLevel + (IS_DEV ? 20 : 0);
    }

    @SubscribeEvent
    public void onGunMelee(GunMeleeEvent event) {
        var shooter = event.getShooter();
        if (shooter.level().isClientSide) {
            return;
        }
        var user = ((PerfectBlockEnchantmentUser) shooter);
        if (!user.amr$canUsePerfectBlock()) {
            return;
        }
        var gun = Gunsmith.getGunInfo(event.getGunItemStack()).orElse(null);
        if (gun == null || GunPredicate.isDedicatedTaCZMeleeWeapon(gun.index())) {
            return;
        }
        var enchantLevel = gun.gunStack().getEnchantmentLevel(this);
        if (enchantLevel <= 0) {
            return;
        }
        var now = shooter.level().getGameTime();
        var period = getDefenseDurationTicks(enchantLevel);
        user.amr$setPerfectBlockEndTime(now + period);
    }

    /**
     * 受伤后5tick内无法完美格挡
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingDamageEvent event) {
        if (!canTrigger(event.getEntity(), event.getSource())) {
            return;
        }
        var now = event.getEntity().level().getGameTime();
        var user = (PerfectBlockEnchantmentUser) event.getEntity();
        user.amr$setPerfectBlockEndTime(0);
        user.amr$setCannotPerfectBlockEndTime(now + CANNOT_BLOCK_DELAY_AFTER_HURT);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canTrigger(LivingEntity user, DamageSource source) {
        if (user.level().isClientSide) {
            return false;
        }
        var badSource = (source.getEntity() == null && source.getDirectEntity() == null)
                || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
        if (badSource) {
            return false;
        }
        // 只对正面方向上的伤害有效
        return isDamageFromFrontSide(user, source)
                // 玩家不能动的情况下不能格挡了
                && user.getAttributeValue(Attributes.MOVEMENT_SPEED) > 0;
    }

    private static boolean isDamageFromFrontSide(LivingEntity user, DamageSource source) {
        var attacker = firstNonNull(source.getDirectEntity(), source.getEntity());
        if (attacker == null) {
            return false;
        }
        var front = user.getLookAngle();
        var isRanged = source.getEntity() != source.getDirectEntity();
        if (isRanged) {
            // 远程攻击的情况下，弹射物速度和玩家朝向相反则判定为正面攻击
            return front.dot(attacker.getDeltaMovement()) < 0;
        } else {
            // 近战攻击，攻击者在玩家前方则为正面攻击
            var offset = attacker.position().subtract(user.position());
            return offset.dot(front) > 0;
        }
    }

    private static <T> @Nullable T firstNonNull(@Nullable T a, @Nullable T b) {
        return a != null ? a : b;
    }

    public static void onPerfectBlockTriggered(LivingEntity user, DamageSource source) {
        if (user.level().isClientSide) {
            return;
        }
        END_BLOCK_ASYNC_TABLE.add(user);
        // 播放格挡音效
        user.level().playSound(null, user, ModContent.Sounds.PERFECT_BLOCK.get(), SoundSource.PLAYERS, 1, 1);
        // 弹飞攻击者
        if (source.getDirectEntity() instanceof LivingEntity swordsman) {
            var knockback = 1.5 + user.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            var knockbackDirection = swordsman.position().subtract(user.position())
                    .with(Direction.Axis.Y, 0)
                    .normalize();
            swordsman.knockback(knockback, -knockbackDirection.x(), -knockbackDirection.z());
        }
        if (source.getDirectEntity() instanceof Projectile projectile) {
            var velocity = projectile.getDeltaMovement();
            var userSize = (user.getBoundingBox().getXsize() + user.getBoundingBox().getZsize()) / 2;
            projectile.setPos(projectile.position().subtract(velocity.normalize().scale(-userSize)));
            PROJECTILE_ASYNC_REFLECT_TABLE.put(projectile, velocity);
        }
    }

    @SubscribeEvent
    public static void onEndOfTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        PROJECTILE_ASYNC_REFLECT_TABLE.forEach((projectile, velocity) -> {
            if (projectile.isAlive()) {
                projectile.setDeltaMovement(velocity.scale(-1));
            }
        });
        PROJECTILE_ASYNC_REFLECT_TABLE.clear();

        END_BLOCK_ASYNC_TABLE.forEach(user -> {
            ((PerfectBlockEnchantmentUser) user).amr$setPerfectBlockEndTime(0);
        });
        END_BLOCK_ASYNC_TABLE.clear();
    }

    private static final Map<Entity, Vec3> PROJECTILE_ASYNC_REFLECT_TABLE = new WeakHashMap<>();
    private static final Set<LivingEntity> END_BLOCK_ASYNC_TABLE = Collections.newSetFromMap(new WeakHashMap<>());
}
