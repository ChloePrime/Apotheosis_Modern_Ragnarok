package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.affix.effect.MobEffectAffix;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AdsPickTargetHookAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.PotionAffixBase;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Map;
import java.util.Set;

/**
 * 瞄准时给被瞄准时的目标上 buff
 */
public class AdsPotionAffix extends PotionAffixBase implements AdsPickTargetHookAffix {
    public static final Codec<AdsPotionAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    affixDef(),
                    BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("mob_effect").forGetter(a -> a.effect),
                    Target.CODEC.fieldOf("target").forGetter(a -> a.target),
                    LootRarity.mapCodec(EffectData.CODEC).fieldOf("values").forGetter(a -> a.values),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types),
                    Codec.BOOL.optionalFieldOf("stack_on_reapply", false).forGetter(a -> a.stackOnReapply))
            .apply(inst, AdsPotionAffix::new));

    public AdsPotionAffix(
            AffixDefinition def,
            Holder<MobEffect> effect,
            Target target,
            Map<LootRarity, EffectData> values,
            Set<LootCategory> types,
            boolean stackOnReapply
    ) {
        super(def, effect, target, values, types, stackOnReapply);
    }

    private static final Target ADS_SELF = Target.ADS_SELF;
    private static final Target ADS_TARGET = Target.ADS_TARGET;

    @Override
    public void onAimingAtEntity(ItemStack stack, Player gunner, AffixInstance instance, EntityHitResult hit) {
        if (!(hit.getEntity() instanceof LivingEntity victim)) {
            return;
        }
        // 瞄准标靶车不触发
        if (victim.getType().is(ModContent.Tags.GUN_IMMUNE)) {
            return;
        }

        if (target == ADS_SELF) {
            applyEffect(gunner, gunner, instance.rarity().get(), instance.level());
        } else if (target == ADS_TARGET) {
            applyEffect(gunner, victim, instance.rarity().get(), instance.level());
        }

    }

    @Override
    public MutableComponent getDescription(AffixInstance affixInstance, AttributeTooltipContext ctx) {
        var rarity = affixInstance.getRarity();
        float level = affixInstance.level();
        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        MutableComponent comp = this.target.toComponent(MobEffectAffix.toComponent(inst, ctx.tickRate()));
        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        return comp;
    }

    @Override
    public Component getAugmentingText(AffixInstance affixInstance, AttributeTooltipContext ctx) {
        var rarity = affixInstance.getRarity();
        float level = affixInstance.level();
        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        MutableComponent comp = this.target.toComponent(MobEffectAffix.toComponent(inst, ctx.tickRate()));

        MobEffectInstance min = this.values.get(rarity).build(this.effect, 0);
        MobEffectInstance max = this.values.get(rarity).build(this.effect, 1);

        if (min.getAmplifier() != max.getAmplifier()) {
            // Vanilla ships potion.potency.0 as an empty string, so we have to fix that here
            Component minComp = min.getAmplifier() == 0 ? Component.literal("I") : Component.translatable("potion.potency." + min.getAmplifier());
            Component maxComp = Component.translatable("potion.potency." + max.getAmplifier());
            comp.append(valueBounds(minComp, maxComp));
        }

        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }

        return comp;
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
