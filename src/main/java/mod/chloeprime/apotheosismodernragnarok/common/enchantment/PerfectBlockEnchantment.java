package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunMeleeEvent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent.SinceMC1211.EnchantmentEffectComponents;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.eventhandlers.GunCritFix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.PerfectBlockEnchantmentUser;
import mod.chloeprime.apotheosismodernragnarok.common.util.MC121Utils;
import mod.chloeprime.apotheosismodernragnarok.common.util.PostureSystem;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.LivingEntityAccessor;
import mod.chloeprime.apotheosismodernragnarok.network.ModNetwork;
import mod.chloeprime.apotheosismodernragnarok.network.S2CExecutionFeedback;
import mod.chloeprime.apotheosismodernragnarok.network.S2CPerfectBlockTriggered;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 完美招架
 */
@EventBusSubscriber
public class PerfectBlockEnchantment {
    public static final long CANNOT_BLOCK_DELAY_AFTER_HURT = 10;
    public static final ResourceKey<DamageType> EXECUTION_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ApotheosisModernRagnarok.loc("execution"));
    private static final boolean IS_DEV = !FMLLoader.isProduction();

    private static long getDefenseTimeWindow(ItemStack stack, RandomSource random) {
        // 开发环境下判定时间+1秒，方便测试
        var base = Math.max(0, Math.round(MC121Utils.evaluateEnchantValue(
                EnchantmentEffectComponents.PERFECT_BLOCK_TIME_WINDOW.get(),
                stack, random, 0
        )));
        if (base == 0) {
            return 0;
        }
        var extra = IS_DEV ? 20 : 0;
        return base + extra;
    }

    @SubscribeEvent
    public static void onGunMelee(GunMeleeEvent event) {
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
        var period = getDefenseTimeWindow(gun.gunStack(), shooter.getRandom());
        if (period <= 0) {
            return;
        }
        var now = shooter.level().getGameTime();
        user.amr$setPerfectBlockEndTime(now + period);
    }

    /**
     * 受伤后5tick内无法完美格挡
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Post event) {
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

    @ApiStatus.Internal
    public static boolean tryExecute(LivingEntity entity, DamageSource source, float attackDamage) {
        // 防止无限递归
        if (EXECUTION_DAMAGE.equals(source.typeHolder())) {
            return true;
        }
        // 环境伤害不触发处决
        if (source.getEntity() == null && source.getDirectEntity() == null) {
            return false;
        }
        // 远程伤害不触发处决
        if (source.getEntity() != source.getDirectEntity()) {
            return false;
        }

        if (!PostureSystem.isPostureBroken(entity)) {
            return false;
        }
        PostureSystem.setPosture(entity, 0);
        return execute(entity, source.getDirectEntity(), source.getEntity(), attackDamage);
    }

    public static boolean execute(LivingEntity victim, @Nullable Entity direct, @Nullable Entity actual, float attackDamage) {
        if (victim instanceof Player player && player.getAbilities().instabuild) {
            return false;
        }
        var attacker = firstNonNull(actual, direct);
        if (attacker != null) {
            attacker.level().playSound(null, attacker, ModContent.Sounds.EXECUTION.get(), attacker.getSoundSource(), 1, 1);
            ModNetwork.sendToNearby(new S2CExecutionFeedback(victim.getId(), attacker.getId()), victim);
        }
        if (CommonConfig.PERFECT_BLOCK_ENABLE_INSTANT_KILL.get()) {
            setHealth(victim, 1e-6F);
            return victim.hurt(victim.damageSources().source(EXECUTION_DAMAGE, direct, actual), Float.MAX_VALUE);
        } else {
            var damage = getExecutionDamage(attacker, attackDamage);
            return victim.hurt(victim.damageSources().source(EXECUTION_DAMAGE, direct, actual), damage);
        }
    }

    @SuppressWarnings("unused")
    private static float getExecutionDamage(Entity attacker, float attackDamage) {
        return (float) Math.pow(attackDamage + 25, 1.5);
    }

    @SuppressWarnings("SameParameterValue")
    private static void setHealth(LivingEntity entity, float value) {
        entity.getEntityData().set(LivingEntityAccessor.getDataHealthId(), Mth.clamp(value, 0, entity.getMaxHealth()));
    }

    @SubscribeEvent
    public static void boostRangedDamageWhenPostureBroken(LivingDamageEvent.Pre event) {
        var victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        // 每tick只执行一次
        if (CRIT_FEEDBACK_RUN_ONCE_BUFFER.add(victim.getId())) {
            var direct = event.getSource().getDirectEntity();
            var actual = event.getSource().getEntity();
            if (direct == actual) {
                return;
            }
            if (PostureSystem.isPostureBroken(victim)) {
                event.setNewDamage(event.getNewDamage() * CommonConfig.POSTURE_BREAK_RANGED_DAMAGE_BONUS.get().floatValue());
                if (actual instanceof LivingEntity attacker) {
                    GunCritFix.criticalFeedback(attacker, victim);
                }
            }
        }
    }

    public static void onPerfectBlockTriggered(LivingEntity user, DamageSource source) {
        if (user.level().isClientSide) {
            return;
        }
        // 以下if内的代码每tick只执行一次
        if (END_BLOCK_ASYNC_TABLE.add(user)) {
            // 阻止该次枪械近战，以避免弹反后立刻造成伤害杀死目标的bug
            IGunOperator.fromLivingEntity(user).getDataHolder().meleePrepTickCount = -1;
            // 播放粒子（RPC）
            ModNetwork.sendToNearby(new S2CPerfectBlockTriggered(user.getId()), user);
            // 弹飞攻击者
            Entity directSource = source.getDirectEntity();
            if (directSource instanceof PartEntity<?> part) {
                directSource = part.getParent();
            }
            if (directSource instanceof LivingEntity swordsman) {
                var knockback = 1.5 + user.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
                var knockbackDirection = swordsman.position().subtract(user.position())
                        .with(Direction.Axis.Y, 0)
                        .normalize();
                swordsman.knockback(knockback, -knockbackDirection.x(), -knockbackDirection.z());
                PostureSystem.onAttackBeingBlocked(swordsman);
            }
            if (directSource instanceof Projectile projectile) {
                projectile.setOwner(user);
                var velocity = projectile.getDeltaMovement();
//                var userSize = (user.getBoundingBox().getXsize() + user.getBoundingBox().getZsize()) / 2;
//                projectile.setPos(projectile.position().subtract(velocity.normalize().scale(-velocity.length() - userSize)));
                PROJECTILE_ASYNC_REFLECT_TABLE.put(projectile, velocity);
            }
            // 播放格挡音效
            SoundEvent sound;
            if (directSource instanceof LivingEntity target) {
                // 判断dead字段来让弹反苦力怕爆炸时播放普通弹刀音效
                sound = target.isAlive() && !((LivingEntityAccessor) target).isDead() && PostureSystem.isPostureBroken(target)
                        ? ModContent.Sounds.POSTURE_BREAK.get()
                        : ModContent.Sounds.PERFECT_BLOCK.get();
            } else {
                sound = ModContent.Sounds.PERFECT_BLOCK.get();
            }
            user.level().playSound(null, user, sound, user.getSoundSource(), 1, 1);
        }
    }

    @SubscribeEvent
    public static void onEndOfTick(ServerTickEvent.Post event) {
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

        CRIT_FEEDBACK_RUN_ONCE_BUFFER.clear();
    }

    private static final IntSet CRIT_FEEDBACK_RUN_ONCE_BUFFER = new IntOpenHashSet();
    private static final Map<Entity, Vec3> PROJECTILE_ASYNC_REFLECT_TABLE = new WeakHashMap<>();
    private static final Set<LivingEntity> END_BLOCK_ASYNC_TABLE = Collections.newSetFromMap(new WeakHashMap<>());
}
