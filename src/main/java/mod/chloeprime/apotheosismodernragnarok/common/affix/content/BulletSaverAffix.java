package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.GunShootEvent;
import dev.shadowsoffire.apotheosis.affix.*;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.BulletSaverAffixUser;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 射击时概率不消耗子弹。
 * <p/>
 * 类型名 apotheosis_modern_ragnarok:bullet_saver
 * 实例名 apotheosis_modern_ragnarok:frugality
 * <p/>
 */
@EventBusSubscriber
public class BulletSaverAffix extends AbstractValuedAffix {

    public static final Codec<BulletSaverAffix> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    affixDef(),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    LootRarity.mapCodec(StepFunction.CODEC).fieldOf("values").forGetter(AbstractValuedAffix::getValues))
            .apply(builder, BulletSaverAffix::new));

    public static final DynamicHolder<BulletSaverAffix> INSTANCE = ModContent.Affix.BULLET_SAVER;

    public BulletSaverAffix(
            AffixDefinition def,
            Set<LootCategory> categories,
            Map<LootRarity, StepFunction> values) {
        super(def, categories, values);
    }

    @SubscribeEvent
    public static void onLivingShoot(GunShootEvent event) {
        if (event.getShooter().level().isClientSide) {
            return;
        }
        var consumesBullet = !check(event.getShooter().getRandom(), event.getGunItemStack());
        ((BulletSaverAffixUser) event.getShooter()).amr$setConsumesBullet(consumesBullet);
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
    public MutableComponent getDescription(AffixInstance affixInstance, AttributeTooltipContext ctx) {
        var stack = affixInstance.stack();
        var rarity = affixInstance.getRarity();
        float level = affixInstance.level();
        var rate = getValue(stack, rarity, level);
        return Component.translatable(desc(), fmtPercent(rate)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(AffixInstance affixInstance, AttributeTooltipContext ctx) {
        var stack = affixInstance.stack();
        var rarity = affixInstance.getRarity();
        float level = affixInstance.level();
        var rate = getValue(stack, rarity, level);
        var min = getValue(stack, rarity, 0);
        var max = getValue(stack, rarity, 1);
        return Component.translatable(desc(), fmtPercents(rate, min, max)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
