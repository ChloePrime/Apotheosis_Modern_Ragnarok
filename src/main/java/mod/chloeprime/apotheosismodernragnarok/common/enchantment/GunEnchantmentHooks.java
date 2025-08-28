package mod.chloeprime.apotheosismodernragnarok.common.enchantment;

import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.gunpack.GunApothData;
import mod.chloeprime.apotheosismodernragnarok.common.util.MC121Utils;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GunEnchantmentHooks {
    private static final ItemStack PREDICATE_SWORD = Items.NETHERITE_SWORD.getDefaultInstance();
    private static final Set<ResourceKey<Enchantment>> BLACKLIST = Set.of(
            Enchantments.SWEEPING_EDGE
    );

    private static final Map<ResourceLocation, Integer> DEFAULT_ENCH_VALUES = Map.of(
            ResourceLocation.fromNamespaceAndPath("tacz", "deagle_golden"), ArmorMaterials.GOLD.value().enchantmentValue()
    );

    public static  int defaultEnchantValue(GunInfo gun) {
        return DEFAULT_ENCH_VALUES.getOrDefault(gun.gunId(), ArmorMaterials.IRON.value().enchantmentValue());
    }

    private static final ThreadLocal<MutableInt> ANTI_RECURSE = ThreadLocal.withInitial(MutableInt::new);
    public static void canGunApplyEnchantmentAtTable(ItemStack stack, Holder<Enchantment> enchantment, BooleanConsumer setReturnValue) {
        var depth = ANTI_RECURSE.get();
        if (depth.intValue() > 0) {
            return;
        }
        try {
            depth.increment();
            canGunApplyEnchantmentAtTable0(stack, enchantment, setReturnValue);
        } finally {
            depth.decrement();
        }
    }

    private static void canGunApplyEnchantmentAtTable0(ItemStack stack, Holder<Enchantment> enchantment, BooleanConsumer cir) {
        var gun = Gunsmith.getGunInfo(stack).orElse(null);
        if (gun == null) {
            return;
        }
        // 黑名单
        var enchantmentId = Optional.ofNullable(enchantment.getKey())
                .map(ResourceKey::location)
                .orElseGet(() -> ApotheosisModernRagnarok.loc("invalid"));
        var isInBlacklist = GunApothData.of(gun)
                .filter(apoth -> apoth.getDisabledEnchantments().contains(enchantmentId))
                .isPresent();
        if (isInBlacklist) {
            cir.accept(false);
        }
        // 近战武器的情况
        if (GunPredicate.isDedicatedTaCZMeleeWeapon(gun.index())) {
            if (MC121Utils.isUnbreakingEnchantment(enchantment)) {
                return;
            }
            cir.accept(GunEnchantmentHooks.isExistingEnchantmentAvailableOnTacMeleeWeapons(enchantment));
        }
        // 枪械
        else {
            if (stack.supportsEnchantment(enchantment)) {
                return;
            }
            var available = GunEnchantmentHooks.isExistingEnchantmentAvailableOnGuns(enchantment) || switch (gun.index().getType()) {
                case "pistol" -> (enchantment.is(ModContent.Enchantments.CAT_PISTOL));
                case "sniper" -> (enchantment.is(ModContent.Enchantments.CAT_SNIPER));
                case "rifle" -> (enchantment.is(ModContent.Enchantments.CAT_RIFLE));
                case "shotgun" -> (enchantment.is(ModContent.Enchantments.CAT_SHOTGUN));
                case "smg" -> (enchantment.is(ModContent.Enchantments.CAT_SMG));
                case "rpg" -> (enchantment.is(ModContent.Enchantments.CAT_HEAVY_WEAPON));
                case "mg" -> (enchantment.is(ModContent.Enchantments.CAT_MACHINE_GUN));
                default -> false;
            };
            if (enchantment.is(ModContent.Enchantments.CAT_MELEE_CAPABLE)) {
                available = available || Optional.ofNullable(gun.index().getGunData().getMeleeData())
                        .map(GunMeleeData::getDefaultMeleeData)
                        .isPresent();
            } else if (enchantment.is(ModContent.Enchantments.CAT_HAS_MAGAZINE)) {
                available = available || gun.index().getGunData().getReloadData().getType() != FeedType.INVENTORY;
            }
            cir.accept(available);
        }
    }

    public static boolean isExistingEnchantmentAvailableOnTacMeleeWeapons(Holder<Enchantment> enchantment) {
        return !BLACKLIST.contains(enchantment.getKey())
                && !MC121Utils.isUnbreakingEnchantment(enchantment)
                && PREDICATE_SWORD.supportsEnchantment(enchantment);
    }

    public static boolean isExistingEnchantmentAvailableOnGuns(Holder<Enchantment> enchantment) {
        return enchantment.is(ModContent.Enchantments.AVAILABLE_FOR_GUNS);
    }
}
