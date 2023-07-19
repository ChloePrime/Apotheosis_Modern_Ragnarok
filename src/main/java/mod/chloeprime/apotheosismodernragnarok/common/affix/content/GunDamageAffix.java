package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.google.gson.JsonObject;
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
 * 提升武器基础伤害
 * <p/>
 * 类型名 apotheosis_modern_ragnarok:damage_bonus
 * 实例名 apotheosis_modern_ragnarok:magnum
 * <p/>
 * @see mod.chloeprime.apotheosismodernragnarok.mixin.tac.MixinGunModifierHelper 实现
 */
public class GunDamageAffix extends AbstractValuedAffix {
    public static final DynamicRegistryObject<GunDamageAffix> INSTANCE = AffixManager.INSTANCE.makeObj(ApotheosisModernRagnarok.loc("magnum"));

    public static float modifyDamage(ItemStack stack, float originalDamage) {
        if (!INSTANCE.isPresent()) {
            return originalDamage;
        }
        var affix = INSTANCE.get();
        return originalDamage * Optional.ofNullable(AffixHelper.getAffixes(stack).get(affix))
                .map(instance -> 1 + affix.getValue(stack, instance))
                .orElse(1F);
    }

    public GunDamageAffix(Pojo data) {
        super(AffixType.STAT, data);
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) { }

    public static GunDamageAffix read(JsonObject obj) {
        return read(obj, GunDamageAffix::new, AbstractValuedAffix.Pojo::new, AbstractValuedAffix::readBase);
    }

    public static GunDamageAffix read(FriendlyByteBuf buf) {
        return read(buf, GunDamageAffix::new, AbstractValuedAffix.Pojo::new, AbstractValuedAffix::readBase);
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
