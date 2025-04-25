package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunMeleeEvent;
import dev.shadowsoffire.apotheosis.Apotheosis;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.internal.PerfectBlockEnchantmentUser;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 完美招架
 */
@Mod.EventBusSubscriber
public class PerfectBlockEnchantment extends Enchantment {
    public static final long CANNOT_BLOCK_DELAY_AFTER_HURT = 10;
    public static final ResourceKey<DamageType> EXECUTION_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ApotheosisModernRagnarok.loc("execution"));
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
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return Apotheosis.enableEnch
                ? 15 + level * level * 4 // 22 ~
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
