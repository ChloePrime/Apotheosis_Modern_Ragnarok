package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.tacz.guns.util.AttachmentDataUtils;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent.SinceMC1211.EnchantmentEffectComponents;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.util.MC121Utils;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

/**
 * 求生本能
 * 击杀怪物后概率掉落子弹
 */
@EventBusSubscriber
public class SurvivalInstinctEnchantment {
    public static final TagKey<DamageType> HAS_BULLET_LOOT_BONUS = TagKey.create(Registries.DAMAGE_TYPE, ApotheosisModernRagnarok.loc("has_bullet_loot_bonus"));

    public static double getDropRate(ItemStack stack, RandomSource random) {
        return Math.max(0, MC121Utils.evaluateEnchantValue(
                EnchantmentEffectComponents.BULLET_DROP_RATE.get(),
                stack, random, 0
        ));
    }


    /**
     * 处决击杀会掉落更多弹药
     */
    public static int getDropBonus(DamageSource source) {
        return source.is(HAS_BULLET_LOOT_BONUS) ? 3 : 1;
    }

    /**
     * 降低弹匣容量<=5的重型武器的子弹掉率
     * @param gun 武器的信息
     * @return 掉率的倍率
     */
    public static double getDropRatePenalty(GunInfo gun) {
        var gunData = gun.index().getGunData();
        if (gunData.getReloadData().getType() == FeedType.FUEL) {
            return 1.0 / AttachmentDataUtils.getAmmoCountWithAttachment(gun.gunStack(), gunData);
        } else {
            return gunData.getAmmoAmount() < 5 ? 1.0 / 16 : 1;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void drops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity user)) {
            return;
        }
        var weapon = user.getMainHandItem();
        var gun = Gunsmith.getGunInfo(weapon).orElse(null);
        if (gun == null || GunPredicate.isDedicatedTaCZMeleeWeapon(gun.index())) {
            return;
        }
        var baseDropRate = getDropRate(weapon, user.getRandom());
        if (baseDropRate <= 1e-8) {
            return;
        }
        // 最大掉落数量 = 怪物最大生命值/伤害 和 枪械弹匣容量 中较小的那个
        var victim = event.getEntity();
        var damage = user.getAttributeValue(GunAttributes.BULLET_DAMAGE);
        int magSize = AttachmentDataUtils.getAmmoCountWithAttachment(gun.gunStack(), gun.index().getGunData());
        int maxTriage = getDropBonus(event.getSource()) * Mth.clamp((int) Math.round(victim.getMaxHealth() / damage), 1, magSize);
        // 计算掉率惩罚时使用原装枪械的弹匣容量进行计算
        var dropRate = baseDropRate * getDropRatePenalty(gun);

        // 计算掉落数量
        var rng = user.getRandom();
        var dropCount = 0;
        for (int i = 0; i < maxTriage; i++) {
            if (rng.nextDouble() <= dropRate) {
                dropCount++;
            }
        }
        if (dropCount == 0) {
            return;
        }

        int ammoStackSize = TimelessAPI.getCommonAmmoIndex(gun.index().getGunData().getAmmoId())
                .map(CommonAmmoIndex::getStackSize)
                .orElse(64);

        // 掉落子弹
        if (dropCount <= ammoStackSize) {
            // 掉落<=64颗子弹时，只生成一个掉落物实体
            var droppedAmmoItem = AmmoItemBuilder.create()
                    .setId(gun.index().getGunData().getAmmoId())
                    .setCount(dropCount)
                    .build();
            var droppedAmmo = new ItemEntity(victim.level(), victim.getX(), victim.getY(), victim.getZ(), droppedAmmoItem);
            event.getDrops().add(droppedAmmo);
        } else {
            // 掉落>64颗子弹时，分批生成凋落物实体
            while (dropCount > 0) {
                var dropped = Math.min(ammoStackSize, dropCount);

                var droppedAmmoItem = AmmoItemBuilder.create()
                        .setId(gun.index().getGunData().getAmmoId())
                        .setCount(dropped)
                        .build();
                var droppedAmmo = new ItemEntity(victim.level(), victim.getX(), victim.getY(), victim.getZ(), droppedAmmoItem);
                event.getDrops().add(droppedAmmo);

                dropCount -= dropped;
            }
        }
    }
}
