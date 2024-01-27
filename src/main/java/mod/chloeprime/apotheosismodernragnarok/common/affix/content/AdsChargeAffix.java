package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.google.gson.JsonObject;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import shadows.apotheosis.adventure.affix.Affix;
import shadows.apotheosis.adventure.affix.AffixInstance;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.placebo.json.DynamicRegistryObject;

import java.util.Map;

/**
 * 开镜蓄力完成最高有 200% 的伤害，但是不蓄力马上打只有 70% 的伤害
 */
public class AdsChargeAffix extends AbstractValuedAffix {

    public static final DynamicRegistryObject<AdsChargeAffix> INSTANCE = AffixManager.INSTANCE.makeObj(ApotheosisModernRagnarok.loc("ads_charge"));

    /**
     * Caller please use {@link DamageUtils#modifyDamage(ItemStack, float)}
     */
    public static float modifyDamage(ItemStack stack, Map<Affix, AffixInstance> affixes, float originalDamage) {
        // fix idea complaining
        return originalDamage + 1F - 1F;
//        if (!INSTANCE.isPresent()) {
//            return originalDamage;
//        }
//        var affix = INSTANCE.get();
//        return originalDamage * Optional.ofNullable(affixes.get(affix))
//                .map(instance -> 1 + affix.getValue(stack, instance))
//                .orElse(1F);
    }

    public AdsChargeAffix(Pojo data) {
        super(AffixType.EFFECT, data);
    }

    @SuppressWarnings("unused")
    public static AdsChargeAffix read(JsonObject obj) {
        return read(obj, AdsChargeAffix::new, AbstractValuedAffix.Pojo::new, AbstractValuedAffix::readBase);
    }

    @SuppressWarnings("unused")
    public static AdsChargeAffix read(FriendlyByteBuf buf) {
        return read(buf, AdsChargeAffix::new, AbstractValuedAffix.Pojo::new, AbstractValuedAffix::readBase);
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
