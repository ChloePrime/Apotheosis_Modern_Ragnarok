package mod.chloeprime.apotheosismodernragnarok.common.gunpack;

import com.google.common.base.Suppliers;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGunData;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GunApothData {
    /**
     * 武器的附魔性能，
     * 默认值为⑨（铁质工具的附魔性能）
     */
    public int enchantment_value = ArmorMaterials.IRON.getEnchantmentValue();

    /**
     * 为 1 时这把武器会被强制判断为近战武器，无论子弹总射程多少。
     * 为 -1 时这把武器会被强制判断为枪械，适合射程特别短的奇葩枪械。
     * 为 0 时保持之前的行为，即根据子弹总射程自动判断
     */
    public int force_melee_weapon;

    /**
     * 为 true 且为近战武器时，这把武器会使用神化重武器（斧头）的词条
     */
    public boolean is_heavy_melee_weapon;

    private ResourceLocation[] disabled_affixes;
    private ResourceLocation[] disabled_enchantments;

    // 配置项结束
    // 以下为逻辑实现部分，枪包作者不用看下去了

    public static Optional<GunApothData> of(ItemStack gun) {
        return Gunsmith.getGunInfo(gun).flatMap(GunApothData::of);
    }

    public static Optional<GunApothData> of(GunInfo gun) {
        return ((EnhancedGunData) gun.index().getGunData()).amr$getApothData();
    }

    public Set<ResourceLocation> getDisabledAffixes() {
        return disabledAffixSet.get();
    }

    public Set<Enchantment> getDisabledEnchantments() {
        return disabledEnchantmentSet.get();
    }

    private transient final Supplier<Set<ResourceLocation>> disabledAffixSet = Suppliers.memoize(
            () -> disabled_affixes != null ? Set.of(disabled_affixes) : Collections.emptySet());

    private transient final Supplier<Set<Enchantment>> disabledEnchantmentSet = Suppliers.memoize(
            () -> disabled_enchantments != null ? mapRegistryObjects(ForgeRegistries.ENCHANTMENTS::getValue, disabled_enchantments) : Collections.emptySet());

    private static <T> Set<T> mapRegistryObjects(Function<ResourceLocation, T> registry, ResourceLocation[] ids) {
        return Arrays.stream(ids).map(registry).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }
}
