package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AdsPickTargetHookAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.GunAffix;
import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GunGemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.util.SocketHelper2;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber
public class GunAffixTrigger {
    @SubscribeEvent
    public static void hurt(EntityHurtByGunEvent event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        var shooter = event.getAttacker();
        if (shooter == null) {
            return;
        }
        var gun = shooter.getMainHandItem();
        if (checkInvalidGun(gun, event.getGunId())) {
            return;
        }

        if (event instanceof EntityHurtByGunEvent.Pre pre) {
            SocketHelper2.forEachValidAffix(gun, (holder, instance) -> {
                if (holder.get() instanceof GunAffix affix) {
                    affix.onGunshotPre(gun, instance, pre);
                }
            });
            SocketHelper2.forEachGemBonus(gun, (gemBonus, gemInstance) -> {
                if (gemBonus instanceof GunGemBonus ggb) {
                    ggb.onGunshotPre(gun, gemInstance.gemStack(), gemInstance, pre);
                }
            });
        }
        if (event instanceof EntityHurtByGunEvent.Post post) {
            SocketHelper2.forEachValidAffix(gun, (holder, instance) -> {
                if (holder.get() instanceof GunAffix affix) {
                    affix.onGunshotPost(gun, instance, post);
                }
            });
            SocketHelper2.forEachGemBonus(gun, (gemBonus, gemInstance) -> {
                if (gemBonus instanceof GunGemBonus ggb) {
                    ggb.onGunshotPost(gun, gemInstance.gemStack(), gemInstance, post);
                }
            });
        }
    }

    @SubscribeEvent
    public static void kill(EntityKillByGunEvent event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        var shooter = event.getAttacker();
        if (shooter == null) {
            return;
        }
        var gun = shooter.getMainHandItem();
        if (checkInvalidGun(gun, event.getGunId())) {
            return;
        }

        SocketHelper2.forEachValidAffix(gun, (holder, instance) -> {
            if (holder.get() instanceof GunAffix affix) {
                affix.onGunshotKill(gun, instance, event);
            }
        });
        SocketHelper2.forEachGemBonus(gun, (gemBonus, gemInstance) -> {
            if (gemBonus instanceof GunGemBonus ggb) {
                ggb.onGunshotKill(gun, gemInstance.gemStack(), gemInstance, event);
            }
        });
    }

    @SubscribeEvent
    public static void onBulletCreate(BulletCreateEvent event) {
        if (event.getBullet().level().isClientSide) {
            return;
        }

        var gun = event.getGun();
        SocketHelper2.forEachValidAffix(gun, (holder, instance) -> {
            if (holder.get() instanceof GunAffix affix) {
                affix.onBulletCreated(gun, instance, event);
            }
        });
        SocketHelper2.forEachGemBonus(gun, (gemBonus, gemInstance) -> {
            if (gemBonus instanceof GunGemBonus ggb) {
                ggb.onBulletCreated(gun, gemInstance.gemStack(), gemInstance, event);
            }
        });
    }

    private static final List<AdsPickTargetHookAffix> AFFIX_BUFFER = new ArrayList<>(8);
    private static final List<AffixInstance> AFFIX_INSTANCE_BUFFER = new ArrayList<>(8);

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide) {
            return;
        }
        var level = ((ServerLevel) event.level);
        if ((level.getGameTime() & 1) == 0) {
            return;
        }
        var simDistanceBlocks = level.getServer().getPlayerList().getSimulationDistance() * 16;
        for (Player player : event.level.players()) {
            if (IGun.mainhandHoldGun(player)) {
                if (IGunOperator.fromLivingEntity(player).getSynAimingProgress() >= 0.9) {
                    tryTriggerAdsPickHook(player, simDistanceBlocks);
                }
            }
        }
    }

    private static void tryTriggerAdsPickHook(Player player, int simDistanceBlocks) {
        var gun = player.getMainHandItem();

        try {
            // 先获取玩家手里有没有瞄准时给予效果类词条
            SocketHelper2.forEachValidAffix(gun, (holder, instance) -> {
                if (holder.get() instanceof AdsPickTargetHookAffix affix && affix.isAdsPickEnabled()) {
                    AFFIX_BUFFER.add(affix);
                    AFFIX_INSTANCE_BUFFER.add(instance);
                }
            });
            // 如果没有此类词条则不执行接下来耗费性能的射线检测操作
            if (AFFIX_BUFFER.isEmpty()) {
                return;
            }

            var eyePos = player.getEyePosition();
            var blockHit = player.pick(simDistanceBlocks, 1, false);

            var entityPickDistance = blockHit.getLocation().distanceTo(eyePos);
            var front = player.getViewVector(1);
            var entityPickEnd = eyePos.add(front.scale(entityPickDistance));
            var aabb = player.getBoundingBox().expandTowards(front.scale(entityPickDistance)).inflate(1, 1, 1);
            var entityHit = ProjectileUtil.getEntityHitResult(
                    player, eyePos, entityPickEnd, aabb,
                    (candidate) -> !candidate.isSpectator() && candidate.isPickable(),
                    entityPickDistance * entityPickDistance
            );
            if (entityHit == null) {
                return;
            }
            // 执行词条效果
            for (int i = 0; i < AFFIX_BUFFER.size(); i++) {
                AFFIX_BUFFER.get(i).onAimingAtEntity(gun, player, AFFIX_INSTANCE_BUFFER.get(i), entityHit);
            }
        } finally {
            AFFIX_BUFFER.clear();
            AFFIX_INSTANCE_BUFFER.clear();
        }
    }

    private static boolean checkInvalidGun(ItemStack weapon, ResourceLocation gunId) {
        return Optional.ofNullable(IGun.getIGunOrNull(weapon))
                .filter(ig -> ig.getGunId(weapon).equals(gunId))
                .isEmpty();
    }
}
