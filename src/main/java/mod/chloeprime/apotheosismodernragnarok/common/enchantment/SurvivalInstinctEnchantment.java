package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * 求生本能
 * 击杀怪物后概率掉落子弹
 */
@ParametersAreNonnullByDefault
public class SurvivalInstinctEnchantment extends Enchantment {
    public SurvivalInstinctEnchantment() {
        this(Rarity.RARE, ModContent.Enchantments.THE_CATEGORY, EquipmentSlot.MAINHAND);
    }

    public SurvivalInstinctEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public int getMaxLevel() {
        return 3;
    }

    public int getMinCost(int level) {
        return 15 + (level - 1) * 9;
    }

    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    public double getDropRate(int level) {
        return 1 - 16.0 / (level + 16);
    }

    /**
     * 降低弹匣容量<=5的重型武器的子弹掉率
     * @param magazineSize 武器的弹匣容量
     * @return 掉率的倍率
     */
    public double getDropRatePenalty(int magazineSize) {
        return magazineSize < 5 ? 1.0 / 16 : 1;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void drops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity user)) {
            return;
        }
        var weapon = user.getMainHandItem();
        var gun = Gunsmith.getGunInfo(weapon).orElse(null);
        if (gun == null || GunPredicate.isMeleeGun(gun.index())) {
            return;
        }
        int level = weapon.getEnchantmentLevel(this);
        if (level <= 0) {
            return;
        }
        // 最大掉落数量 = 怪物最大生命值/伤害 和 枪械弹匣容量 中较小的那个
        var victim = event.getEntity();
        var damage = user.getAttributeValue(GunAttributes.BULLET_DAMAGE.get());
        int magSize = AttachmentDataUtils.getAmmoCountWithAttachment(gun.gunStack(), gun.index().getGunData());
        int maxTriage = Mth.clamp((int) Math.round(victim.getMaxHealth() / damage), 1, magSize);
        // 计算掉率惩罚时使用原装枪械的弹匣容量进行计算
        var dropRate = getDropRate(level) * getDropRatePenalty(gun.index().getGunData().getAmmoAmount());

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

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other) && !(other instanceof ProjectionMagicEnchantment);
    }
}
