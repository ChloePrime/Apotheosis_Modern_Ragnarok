package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.*;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;
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
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    GemBonus.VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues))
            .apply(builder, BulletSaverAffix::new));

    public static final DynamicHolder<BulletSaverAffix> INSTANCE
            = AffixRegistry.INSTANCE.holder(ApotheosisModernRagnarok.loc("frugality"));

    public BulletSaverAffix(
            Set<LootCategory> categories,
            Map<LootRarity, StepFunction> values) {
        super(AffixType.ABILITY, categories, values);
    }

    public static boolean check(RandomSource context, ItemStack stack) {
        return Optional.ofNullable(AffixHelper.getAffixes(stack).get(ModContent.Affix.BULLET_SAVER))
                .map(instance -> instance.affix().get() instanceof BulletSaverAffix affix && affix.check(context, stack, instance))
                .orElse(false);
    }

    public boolean check(RandomSource context, ItemStack stack, AffixInstance instance) {
        return context.nextFloat() <= getValue(stack, instance.rarity().get(), instance.level());
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
