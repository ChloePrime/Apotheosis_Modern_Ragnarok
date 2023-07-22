package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.google.gson.JsonObject;
import com.tac.guns.item.GunItem;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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

    public static final DynamicRegistryObject<AmmoCapacityAffix> INSTANCE = AffixManager.INSTANCE.makeObj(ApotheosisModernRagnarok.loc("clip_expansion"));

    @Override
    public boolean canApplyTo(ItemStack stack, LootRarity rarity) {
        return super.canApplyTo(stack, rarity) && !hasOnlyOneAmmoPerClip(stack);
    }

    private static boolean hasOnlyOneAmmoPerClip(ItemStack stack) {
        if (!(stack.getItem() instanceof GunItem gunItem)) {
            return false;
        }
        return gunItem.getGun().getReloads().getMaxAmmo() == 1;
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

    public AmmoCapacityAffix(Pojo data) {
        super(AffixType.STAT, data);
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) { }

    public static AmmoCapacityAffix read(JsonObject obj) {
        return read(obj, AmmoCapacityAffix::new, AbstractValuedAffix.Pojo::new, AbstractValuedAffix::readBase);
    }

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
