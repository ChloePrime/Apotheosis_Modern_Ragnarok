package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Set;
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

    public static final Codec<BulletSaverAffix> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    ExtraCodecs.LOOT_CATEGORY_SET.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    GemBonus.VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues))
            .apply(builder, BulletSaverAffix::new));

    public static final DynamicHolder<BulletSaverAffix> INSTANCE
            = AffixRegistry.INSTANCE.holder(ApotheosisModernRagnarok.loc("frugality"));

    public BulletSaverAffix(
            Set<LootCategory> categories,
            Map<LootRarity, StepFunction> values) {
        super(AffixType.ABILITY, categories, values);
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        var percent = 100 * getValue(stack, rarity, level);
        list.accept(Component.translatable(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
