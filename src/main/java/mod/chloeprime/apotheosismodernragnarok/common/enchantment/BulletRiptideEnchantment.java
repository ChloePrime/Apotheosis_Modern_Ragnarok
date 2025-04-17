package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedKineticBullet;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@MethodsReturnNonnullByDefault
public class BulletRiptideEnchantment extends Enchantment {
    public BulletRiptideEnchantment() {
        this(Rarity.UNCOMMON, ModContent.Enchantments.THE_CATEGORY, EquipmentSlot.MAINHAND);
    }

    public BulletRiptideEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinCost(int level) {
        return 10 + level * 15;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return 50000;
    }

    public double getFrictionFactor(int level) {
        return 1.0 / (1 + level);
    }

    @SubscribeEvent
    public void onBulletCreate(BulletCreateEvent event) {
        var level = event.getGun().getEnchantmentLevel(this);
        if (level <= 0) {
            return;
        }
        if (!(event.getBullet() instanceof EnhancedKineticBullet bullet)) {
            return;
        }
        bullet.amr$applyWaterFrictionFactor(getFrictionFactor(level));
    }
}