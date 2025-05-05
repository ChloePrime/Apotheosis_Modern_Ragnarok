package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;
import dev.shadowsoffire.apotheosis.ench.enchantments.masterwork.KnowledgeEnchant;
import dev.shadowsoffire.apotheosis.ench.enchantments.masterwork.ScavengerEnchant;
import dev.shadowsoffire.apotheosis.spawn.enchantment.CapturingEnchant;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.gunpack.GunApothData;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;

@Mod.EventBusSubscriber
public class GunEnchantmentHooks {
    private static final ItemStack PREDICATE_SWORD = Items.NETHERITE_SWORD.getDefaultInstance();
    private static final Set<Enchantment> BLACKLIST = Set.of(
            Enchantments.SWEEPING_EDGE
    );

    public static void canGunApplyEnchantmentAtTable(Item item, ItemStack stack, Enchantment enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (!(item instanceof IGun)) {
            return;
        }
        var gun = Gunsmith.getGunInfo(stack).orElse(null);
        if (gun == null) {
            return;
        }
        // 黑名单
        var isInBlacklist = GunApothData.of(gun)
                .filter(apoth -> apoth.getDisabledEnchantments().contains(enchantment))
                .isPresent();
        if (isInBlacklist) {
            cir.setReturnValue(false);
        }
        // 近战武器的情况
        if (GunPredicate.isDedicatedTaCZMeleeWeapon(gun.index())) {
            if (enchantment.category == EnchantmentCategory.BREAKABLE) {
                return;
            }
            cir.setReturnValue(GunEnchantmentHooks.isExistingEnchantmentAvailableOnTacMeleeWeapons(enchantment));
        }
        // 枪械
        else {
            if (enchantment.category.canEnchant(stack.getItem())) {
                return;
            }
            var available = GunEnchantmentHooks.isExistingEnchantmentAvailableOnGuns(enchantment) || switch (gun.index().getType()) {
                case "pistol" -> (enchantment.category == ModContent.Enchantments.CAT_PISTOL);
                case "sniper" -> (enchantment.category == ModContent.Enchantments.CAT_SNIPER);
                case "rifle" -> (enchantment.category == ModContent.Enchantments.CAT_RIFLE);
                case "shotgun" -> (enchantment.category == ModContent.Enchantments.CAT_SHOTGUN);
                case "smg" -> (enchantment.category == ModContent.Enchantments.CAT_SMG);
                case "rpg" -> (enchantment.category == ModContent.Enchantments.CAT_HEAVY_WEAPON);
                case "mg" -> (enchantment.category == ModContent.Enchantments.CAT_MACHINE_GUN);
                default -> false;
            };
            if (enchantment.category == ModContent.Enchantments.CAT_MELEE_CAPABLE) {
                available = available || Optional.ofNullable(gun.index().getGunData().getMeleeData())
                        .map(GunMeleeData::getDefaultMeleeData)
                        .isPresent();
            } else if (enchantment.category == ModContent.Enchantments.CAT_HAS_MAGAZINE) {
                available = available || gun.index().getGunData().getReloadData().getType() != FeedType.INVENTORY;
            }
            cir.setReturnValue(available);
        }
    }

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
        if (IGun.getIGunOrNull(weapon) == null || !GunPredicate.isDedicatedTaCZMeleeWeapon(weapon)) {
            return;
        }
        int i = EnchantmentHelper.getFireAspect(attacker);
        if (i > 0) {
            event.getEntity().setSecondsOnFire(i * 4);
        }
    }
}
