package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.mixin.tacz.EntityKineticBulletAccessor;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Optional;

@Mod.EventBusSubscriber
public class LeftButtonMeleeFix {
    public static final TagKey<DamageType> TACZ_BULLETS = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullets"));

    /**
     * 如果用 HIGHEST 的话会在 GunsmithLib 后面执行，
     * 导致依然会计算射击伤害而不是攻击伤害。
     * 所以这里用 mixin 让事件以比 HIGHEST 更高的优先级执行。
     *
     * @see mod.chloeprime.apotheosismodernragnarok.mixin.tacz.MixinEntityKineticBullet
     */
    public static void fixDamageTypesOnGunshotPre(EntityHurtByGunEvent.Pre event) {
        if (!isDedicatedMeleeWeapon(event.getGunId())) {
            return;
        }
        var oldSource1 = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        var oldSource2 = event.getDamageSource(GunDamageSourcePart.ARMOR_PIERCING);

        var newType1 = oldSource1.is(TACZ_BULLETS) ? getMeleeDamageSource(event.getAttacker(), false).orElseGet(oldSource1::typeHolder) : oldSource1.typeHolder();
        var newType2 = oldSource2.is(TACZ_BULLETS) ? getMeleeDamageSource(event.getAttacker(), true).orElseGet(oldSource2::typeHolder) : oldSource2.typeHolder();

        var newSource1 = new DamageSource(newType1, oldSource1.getEntity(), oldSource1.getEntity(), oldSource1.sourcePositionRaw());
        var newSource2 = new DamageSource(newType2, oldSource2.getEntity(), oldSource2.getEntity(), oldSource2.sourcePositionRaw());

        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING, newSource1);
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING, newSource2);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void fixRangeOnBulletCreate(BulletCreateEvent event) {
        var attacker = event.getShooter();
        var bullet = event.getBullet();
        if (!(bullet instanceof EntityKineticBulletAccessor accessor)) {
            return;
        }
        Vec3 velocity = bullet.getDeltaMovement();
        double oldSpeed = velocity.length();
        int life = accessor.getLife() - 1;
        if (life <= 0 || Math.abs(oldSpeed) <= 1e-3) {
            return;
        }
        double oldRange = oldSpeed * life;
        double newRange = GsHelper.getAttributeValueWithBase(attacker, ForgeMod.ENTITY_REACH.get(), oldRange);
        if (Math.abs(newRange - oldRange) <= 1e-3) {
            return;
        }
        double newSpeed = newRange / life;
        bullet.setDeltaMovement(velocity.scale(newSpeed / oldSpeed));
    }

    private static Optional<Holder<DamageType>> getMeleeDamageSource(LivingEntity attacker, boolean ap) {
        if (attacker == null) {
            return Optional.empty();
        }
        var key = attacker instanceof Player
                ? (ap ? ModContent.DamageTypes.PLAYER_ARMOR_PIERCING_ATTACK : DamageTypes.PLAYER_ATTACK)
                : (ap ? ModContent.DamageTypes.MOB_ARMOR_PIERCING_ATTACK :DamageTypes.MOB_ATTACK);
        return attacker.level().registryAccess()
                .registry(Registries.DAMAGE_TYPE)
                .flatMap(registry -> registry.getHolder(key));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGunshotPost(EntityHurtByGunEvent.Post event) {
        onGunshotPostOrKill(event.getGunId(), event.getAttacker(), event.getHurtEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGunshotKill(EntityKillByGunEvent event) {
        onGunshotPostOrKill(event.getGunId(), event.getAttacker(), event.getKilledEntity());
    }

    private static void onGunshotPostOrKill(ResourceLocation gunId, @Nullable LivingEntity user, Entity target) {
        if (!isDedicatedMeleeWeapon(gunId)) {
            return;
        }
        if (user == null || !(target instanceof LivingEntity victim)) {
            return;
        }
        user.doEnchantDamageEffects(user, victim);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isDedicatedMeleeWeapon(ResourceLocation gunId) {
        return TimelessAPI.getCommonGunIndex(gunId)
                .filter(GunPredicate::isDedicatedTaCZMeleeWeapon)
                .isPresent();
    }
}
