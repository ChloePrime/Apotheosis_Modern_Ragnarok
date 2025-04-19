package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.GunShootEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.*;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.BulletSaverAffixUser;
import mod.chloeprime.apotheosismodernragnarok.mixin.tacz.MixinModernKineticGunScriptAPI.BulletSaverAffixMixin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 射击时概率不消耗子弹。
 * <p/>
 * 类型名 apotheosis_modern_ragnarok:bullet_saver
 * 实例名 apotheosis_modern_ragnarok:frugality
 * <p/>
 * @see BulletSaverAffixMixin 实现
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
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        var rate = getValue(stack, rarity, level);
        return Component.translatable(desc(), fmtPercent(rate)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public Component getAugmentingText(ItemStack stack, LootRarity rarity, float level) {
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
