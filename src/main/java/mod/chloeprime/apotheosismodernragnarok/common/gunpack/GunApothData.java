package mod.chloeprime.apotheosismodernragnarok.common.gunpack;

import net.minecraft.world.item.ArmorMaterials;

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
}
