package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.PotionAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AdsPickTargetHookAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.PotionAffixBase;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * 瞄准时给被瞄准时的目标上 buff
 */
public class AdsPotionAffix extends PotionAffixBase implements AdsPickTargetHookAffix {
    public static final Codec<AdsPotionAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("mob_effect").forGetter(a -> a.effect),
                    LootRarity.mapCodec(EffectData.CODEC).fieldOf("values").forGetter(a -> a.values),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types),
                    PlaceboCodecs.nullableField(Codec.BOOL, "stack_on_reapply", false).forGetter(a -> a.stackOnReapply))
            .apply(inst, AdsPotionAffix::new));

    public AdsPotionAffix(MobEffect effect, Map<LootRarity, EffectData> values, Set<LootCategory> types, boolean stackOnReapply) {
        super(AffixType.ABILITY, effect, ADS_TARGET, values, types, stackOnReapply);
    }

    private static final Target ADS_TARGET = Target.create("ADS_TARGET", "ads_target");

    @Override
    public void onAimingAtEntity(ItemStack stack, AffixInstance instance, EntityHitResult hit) {
        if (!(hit.getEntity() instanceof LivingEntity victim)) {
            return;
        }
        applyEffect(victim, instance.rarity().get(), instance.level());
    }

    @Override
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        MutableComponent comp = this.target.toComponent(PotionAffix.toComponent(inst));
        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        return comp;
    }

    @Override
    public Component getAugmentingText(ItemStack stack, LootRarity rarity, float level) {
        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        MutableComponent comp = this.target.toComponent(PotionAffix.toComponent(inst));

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
