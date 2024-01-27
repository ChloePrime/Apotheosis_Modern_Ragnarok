package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.google.gson.JsonObject;
import com.tac.guns.item.GunItem;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunCategories;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.json.DynamicRegistryObject;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * 弹匣容量
 * <p/>
 * @see mod.chloeprime.apotheosismodernragnarok.mixin.tac.MixinGunModifierHelper 实现
 */
public class AmmoCapacityAffix extends AbstractValuedAffix {
    public static final TagKey<Item> NERF_BONUS_TAG = GunCategories.Tags.NERF_CALIBER_BONUS;
    public static final TagKey<Item> DISABLE_BONUS_TAG = GunCategories.Tags.DISABLE_CALIBER_BONUS;
    public static final DynamicRegistryObject<AmmoCapacityAffix> INSTANCE = AffixManager.INSTANCE.makeObj(ApotheosisModernRagnarok.loc("clip_expansion"));

    @Override
    public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
        return super.canApplyTo(stack, rarity) && !stack.is(DISABLE_BONUS_TAG);
    }

    public static int modifyAmmoCapacity(ItemStack stack, int originalCapacity) {
        if (!INSTANCE.isPresent()) {
            return originalCapacity;
        }
        var affix = INSTANCE.get();
        return originalCapacity + Optional.ofNullable(AffixHelper.getAffixes(stack).get(affix))
                .map(instance -> Math.round(affix.getValue(stack, instance)))
                .orElse(0);
    }

    @Override
    public float getValue(ItemStack gun, LootRarity rarity, float level) {
        return super.getValue(gun, rarity, level) * getScale(gun);
    }

    /**
     * 削弱非全自动武器的弹匣加成
     */
    private float getScale(ItemStack gun) {
        var nerfedScale = 0.2F;
        if (gun.is(NERF_BONUS_TAG)) {
            return nerfedScale;
        }
        return gun.getItem() instanceof GunItem gunItem
                ? gunItem.getGun().getGeneral().isAuto() ? 1 : nerfedScale
                : 1;
    }

    public AmmoCapacityAffix(Pojo data) {
        super(AffixType.STAT, data);
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) { }

    @SuppressWarnings("unused")
    public static AmmoCapacityAffix read(JsonObject obj) {
        return read(obj, AmmoCapacityAffix::new, AbstractValuedAffix.Pojo::new, AbstractValuedAffix::readBase);
    }

    @SuppressWarnings("unused")
    public static AmmoCapacityAffix read(FriendlyByteBuf buf) {
        return read(buf, AmmoCapacityAffix::new, AbstractValuedAffix.Pojo::new, AbstractValuedAffix::readBase);
    }

    @Override
    public JsonObject write() {
        return super.write();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
    }
}
