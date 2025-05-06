package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.PotionAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunPredicate;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.PotionAffixBase;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

public class RatedPotionAffix extends PotionAffixBase {

    public static final Codec<RatedPotionAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("mob_effect").forGetter(a -> a.effect),
                    Target.CODEC.fieldOf("target").forGetter(a -> a.target),
                    LootRarity.mapCodec(EffectData.CODEC).fieldOf("values").forGetter(a -> a.values),
                    PlaceboCodecs.nullableField(Codec.FLOAT, "rate", 0F).forGetter(a -> a.rate),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types),
                    PlaceboCodecs.nullableField(Codec.BOOL, "stack_on_reapply", false).forGetter(a -> a.stackOnReapply))
            .apply(inst, RatedPotionAffix::new));

    protected final float rate;

    public RatedPotionAffix(MobEffect effect, Target target, Map<LootRarity, EffectData> values, float rate, Set<LootCategory> types, boolean stackOnReapply) {
        super(AffixType.ABILITY, effect, target, values, types, stackOnReapply);
        this.rate = rate;
    }

    @Override
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        var rate = getTriggerRate(rarity, level);
        MutableComponent comp = this.target.toComponent("%.2f%%".formatted(100 * rate), PotionAffix.toComponent(inst));
        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        return comp;
    }

    @Override
    public Component getAugmentingText(ItemStack stack, LootRarity rarity, float level) {
        var rate = this.getTriggerRate(rarity, level);
        var minRate = this.getTriggerRate(rarity, 0);
        var maxRate = this.getTriggerRate(rarity, 1);

        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        MutableComponent comp = this.target.toComponent(fmtPercents(rate, minRate, maxRate), PotionAffix.toComponent(inst));

        MobEffectInstance min = this.values.get(rarity).build(this.effect, 0);
        MobEffectInstance max = this.values.get(rarity).build(this.effect, 1);

        if (min.getAmplifier() != max.getAmplifier()) {
            // Vanilla ships potion.potency.0 as an empty string, so we have to fix that here
            Component minComp = min.getAmplifier() == 0 ? Component.literal("I") : Component.translatable("potion.potency." + min.getAmplifier());
            Component maxComp = Component.translatable("potion.potency." + max.getAmplifier());
            comp.append(valueBounds(minComp, maxComp));
        }

        if (!this.effect.isInstantenous() && min.getDuration() != max.getDuration()) {
            Component minComp = MobEffectUtil.formatDuration(min, 1);
            Component maxComp = MobEffectUtil.formatDuration(max, 1);
            comp.append(valueBounds(minComp, maxComp));
        }

        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }

        return comp;
    }

    protected float getTriggerRate(LootRarity rarity, float level) {
        return this.values.get(rarity).rate().get(level);
    }

    @Override
    public void applyEffect(LivingEntity owner, LivingEntity target, LootRarity rarity, float level) {
        if (target.level().isClientSide()) {
            super.applyEffect(owner, target, rarity, level);
            return;
        }

        // 对于左键近战武器来说，把概率分摊到每个弹片上
        double coefficient = switch (this.target) {
            case ARROW_SELF, ARROW_TARGET -> GunPredicate.getBuffCoefficient(owner.getMainHandItem());
            default -> 1;
        };
        // 对于左键近战武器来说，把概率分摊到每个弹片上

        // 概率检定
        if (target.getRandom().nextFloat() > coefficient * getTriggerRate(rarity, level)) {
            return;
        }

        super.applyEffect(owner, target, rarity, level);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

}
