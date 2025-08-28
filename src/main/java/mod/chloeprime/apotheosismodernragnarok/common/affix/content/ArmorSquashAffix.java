package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.api.events.ArmorSquashAffixTakeEffectEvent;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.GunAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * 命中时概率碎甲。
 * 在栓动大威力步枪上的碎甲概率大幅增加。
 * <p>
 * 类型名 apotheosis_modern_ragnarok:armor_squash
 * 实例名 apotheosis_modern_ragnarok:armor_squash
 * <p/>
 */
public class ArmorSquashAffix extends AbstractValuedAffix implements GunAffix {

    public static final Codec<ArmorSquashAffix> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    affixDef(),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    LootRarity.mapCodec(StepFunction.CODEC).fieldOf("values").forGetter(AbstractValuedAffix::getValues),
                    ExtraCodecs.COEFFICIENT_BY_CATEGORY.fieldOf("coefficients").forGetter(a -> a.coefficients))
            .apply(builder, ArmorSquashAffix::new));

    public ArmorSquashAffix(
            AffixDefinition def,
            Set<LootCategory> categories,
            Map<LootRarity, StepFunction> values,
            Map<LootCategory, Double> coefficients) {
        super(def, categories, values);
        this.coefficients = coefficients;
    }

    public Map<LootCategory, Double> getCoefficients() {
        return coefficients;
    }

    @Override
    public double getValue(ItemStack gun, LootRarity rarity, float level) {
        return getCaliberBonus(gun) * super.getValue(gun, rarity, level);
    }

    @Override
    public MutableComponent getDescription(AffixInstance affixInstance, AttributeTooltipContext ctx) {
        var stack = affixInstance.stack();
        var rarity = affixInstance.getRarity();
        float level = affixInstance.level();
        var percent = getValue(stack, rarity, level);
        return Component.translatable(desc(), fmtPercent(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
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

    public final double getCaliberBonus(ItemStack stack) {
        return getCoefficients().getOrDefault(LootCategory.forItem(stack), 1.0);
    }

    @Override
    public void onGunshotPost(ItemStack gun, AffixInstance instance, EntityHurtByGunEvent.Post event) {
        Optional.ofNullable(event.getHurtEntity()).ifPresent(victim -> {
            if (CommonConfig.isArmorSquashBlacklist(victim)) {
                return;
            }
            // 射击标靶车不触发
            if (victim.getType().is(ModContent.Tags.GUN_IMMUNE)) {
                return;
            }
            Optional.ofNullable(event.getAttacker())
                    .ifPresent(attacker -> onLivingHurt0(victim, gun, instance, attacker));
        });
    }

    private void onLivingHurt0(Entity victim, ItemStack gun, AffixInstance instance, Entity attacker) {
        if (!(victim instanceof LivingEntity livingVictim)) {
            return;
        }
        // 概率检定
        var coefficient = GunPredicate.getBuffCoefficient(gun);
        if (livingVictim.getRandom().nextFloat() > getValue(gun, instance) * coefficient) {
            return;
        }
        // 寻找一件护甲
        StreamSupport.stream(livingVictim.getArmorSlots().spliterator(), false)
                .filter(stack -> !stack.isEmpty())
                .findAny()
                .ifPresent(armor -> {
                    // 打碎护甲
                    onArmorBreak(livingVictim, attacker, this, armor);
                });
    }

    private final Map<LootCategory, Double> coefficients;

    private static void onArmorBreak(LivingEntity victim, Entity source, ArmorSquashAffix affix, ItemStack armor) {
        // runs on the server :)
        if (NeoForge.EVENT_BUS.post(new ArmorSquashAffixTakeEffectEvent(victim, source, affix, armor)).isCanceled()) {
            return;
        }
        armor.shrink(1);
        victim.level().playSound(null, victim, ModContent.Sounds.ARMOR_CRACK.get(), victim.getSoundSource(), 1, 1);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
