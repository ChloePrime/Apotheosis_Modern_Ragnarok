package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.ench.enchantments.masterwork.KnowledgeEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.masterwork.ScavengerEnchant;
import dev.shadowsoffire.apotheosis.spawn.enchantment.CapturingEnchant;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber
public class GunEnchantmentHooks {
    private static final ItemStack PREDICATE_SWORD = Items.NETHERITE_SWORD.getDefaultInstance();
    private static final Set<Enchantment> BLACKLIST = Set.of(
            Enchantments.SWEEPING_EDGE
    );

    public static boolean isExistingEnchantmentAvailableOnTacMeleeWeapons(Enchantment enchantment) {
        return !BLACKLIST.contains(enchantment)
                && enchantment.category != EnchantmentCategory.BREAKABLE
                && PREDICATE_SWORD.canApplyAtEnchantingTable(enchantment);
    }

    public static boolean isExistingEnchantmentAvailableOnGuns(Enchantment enchantment) {
        return enchantment == Enchantments.MOB_LOOTING
                || enchantment instanceof ScavengerEnchant
                || enchantment instanceof KnowledgeEnchant
                || enchantment instanceof CapturingEnchant;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void fireAspectCompatForMeleeGuns(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        var weapon = attacker.getMainHandItem();
        if (IGun.getIGunOrNull(weapon) == null || !GunPredicate.isMeleeGun(weapon)) {
            return;
        }
        int i = EnchantmentHelper.getFireAspect(attacker);
        if (i > 0) {
            event.getEntity().setSecondsOnFire(i * 4);
        }
    }
}
