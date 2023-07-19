package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.google.gson.JsonObject;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.json.DynamicRegistryObject;

import java.util.function.Consumer;

/**
 * 射击时概率不消耗子弹。
 * <p/>
 * 类型名 apotheosis_modern_ragnarok:bullet_saver
 * 实例名 apotheosis_modern_ragnarok:frugality
 * <p/>
 * @see mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis.MixinTelepathicAffix 实现
 */
@Mod.EventBusSubscriber
public class BulletSaverAffix extends AbstractValuedAffix {
    public static final DynamicRegistryObject<BulletSaverAffix> INSTANCE
            = AffixManager.INSTANCE.makeObj(ApotheosisModernRagnarok.loc("frugality"));

    public BulletSaverAffix(AbstractValuedAffix.Pojo data) {
        super(AffixType.EFFECT, data);
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        var percent = 100 * getValue(stack, rarity, level);
        list.accept(new TranslatableComponent(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
    }

    public static BulletSaverAffix read(JsonObject obj) {
        return read(obj, BulletSaverAffix::new, Pojo::new, AbstractValuedAffix::readBase);
    }

    public static BulletSaverAffix read(FriendlyByteBuf buf) {
        return read(buf, BulletSaverAffix::new, Pojo::new, AbstractValuedAffix::readBase);
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
