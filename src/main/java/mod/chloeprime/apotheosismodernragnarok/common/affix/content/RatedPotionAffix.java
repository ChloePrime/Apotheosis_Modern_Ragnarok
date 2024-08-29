package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.PotionAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.common.affix.GunAffix;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class RatedPotionAffix extends Affix implements GunAffix {

    public static final Codec<RatedPotionAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("mob_effect").forGetter(a -> a.effect),
                    Target.CODEC.fieldOf("target").forGetter(a -> a.target),
                    LootRarity.mapCodec(EffectData.CODEC).fieldOf("values").forGetter(a -> a.values),
                    PlaceboCodecs.nullableField(Codec.FLOAT, "rate", 0F).forGetter(a -> a.rate),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types),
                    PlaceboCodecs.nullableField(Codec.BOOL, "stack_on_reapply", false).forGetter(a -> a.stackOnReapply))
            .apply(inst, RatedPotionAffix::new));

    protected final MobEffect effect;
    protected final Target target;
    protected final Map<LootRarity, EffectData> values;
    protected final Set<LootCategory> types;
    protected final float rate;
    protected final boolean stackOnReapply;

    public RatedPotionAffix(MobEffect effect, Target target, Map<LootRarity, EffectData> values, float rate, Set<LootCategory> types, boolean stackOnReapply) {
        super(AffixType.ABILITY);
        this.effect = effect;
        this.target = target;
        this.values = values;
        this.rate = rate;
        this.types = types;
        this.stackOnReapply = stackOnReapply;
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        MobEffectInstance inst = this.values.get(rarity).build(this.effect, level);
        var rate = getTriggerRate(rarity, level);
        MutableComponent comp = this.target.toComponent("%.2f%%".formatted(100 * rate), PotionAffix.toComponent(inst));
        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        list.accept(comp);
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return (this.types.isEmpty() || this.types.contains(cat)) && this.values.containsKey(rarity);
    }

    @Override
    public void doPostHurt(ItemStack stack, LootRarity rarity, float level, LivingEntity user, Entity attacker) {
        if (this.target == Target.HURT_SELF) this.applyEffect(user, rarity, level);
        else if (this.target == Target.HURT_ATTACKER) {
            if (attacker instanceof LivingEntity tLiving) {
                this.applyEffect(tLiving, rarity, level);
            }
        }
    }

    @Override
    public void doPostAttack(ItemStack stack, LootRarity rarity, float level, LivingEntity user, Entity target) {
        if (this.target == Target.ATTACK_SELF) this.applyEffect(user, rarity, level);
        else if (this.target == Target.ATTACK_TARGET) {
            if (target instanceof LivingEntity tLiving) {
                this.applyEffect(tLiving, rarity, level);
            }
        }
    }

    @Override
    public void onBlockBreak(ItemStack stack, LootRarity rarity, float level, Player player, LevelAccessor world, BlockPos pos, BlockState state) {
        if (this.target == Target.BREAK_SELF) {
            this.applyEffect(player, rarity, level);
        }
    }

    @Override
    public void onArrowImpact(AbstractArrow arrow, LootRarity rarity, float level, HitResult res, HitResult.Type type) {
        if (this.target == Target.ARROW_SELF) {
            if (arrow.getOwner() instanceof LivingEntity owner) {
                this.applyEffect(owner, rarity, level);
            }
        }
        else if (this.target == Target.ARROW_TARGET) {
            if (type == HitResult.Type.ENTITY && ((EntityHitResult) res).getEntity() instanceof LivingEntity target) {
                this.applyEffect(target, rarity, level);
            }
        }
    }

    public void onGunshotPost(ItemStack stack, AffixInstance instance, EntityHurtByGunEvent.Post event) {
        if (this.target == Target.ARROW_SELF) {
            Optional.ofNullable(event.getAttacker()).ifPresent(owner -> this.applyEffect(owner, instance.rarity().get(), instance.level()));
        }
        else if (this.target == Target.ARROW_TARGET) {
            if (event.getHurtEntity() instanceof LivingEntity victim) {
                this.applyEffect(victim, instance.rarity().get(), instance.level());
            }
        }
    }

    @Unique
    @Override
    public void onGunshotKill(ItemStack stack, AffixInstance instance, EntityKillByGunEvent event) {
        if (this.target == Target.ARROW_SELF) {
            Optional.ofNullable(event.getAttacker()).ifPresent(owner -> this.applyEffect(owner, instance.rarity().get(), instance.level()));
        }
    }

    @Override
    public float onShieldBlock(ItemStack stack, LootRarity rarity, float level, LivingEntity entity, DamageSource source, float amount) {
        if (this.target == Target.BLOCK_SELF) {
            this.applyEffect(entity, rarity, level);
        }
        else if (this.target == Target.BLOCK_ATTACKER && source.getDirectEntity() instanceof LivingEntity target) {
            this.applyEffect(target, rarity, level);
        }
        return amount;
    }

    protected float getTriggerRate(LootRarity rarity, float level) {
        return this.values.get(rarity).rate.get(level);
    }

    private void applyEffect(LivingEntity target, LootRarity rarity, float level) {
        if (target.level().isClientSide()) return;

        // 概率检定
        if (target.getRandom().nextFloat() > getTriggerRate(rarity, level)) {
            return;
        }

        EffectData data = this.values.get(rarity);
        var inst = target.getEffect(this.effect);
        if (this.stackOnReapply && inst != null) {
            var newInst = new MobEffectInstance(this.effect, (int) Math.max(inst.getDuration(), data.duration.get(level)), (int) (inst.getAmplifier() + 1 + data.amplifier.get(level)));
            target.addEffect(newInst);
        }
        else {
            target.addEffect(data.build(this.effect, level));
        }
        startCooldown(this.getId(), target);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

    public record EffectData(StepFunction duration, StepFunction amplifier, StepFunction rate) {
        private static final Codec<EffectData> CODEC = RecordCodecBuilder.create(inst -> inst
                .group(
                        StepFunction.CODEC.fieldOf("duration").forGetter(EffectData::duration),
                        StepFunction.CODEC.fieldOf("amplifier").forGetter(EffectData::amplifier),
                        StepFunction.CODEC.fieldOf("rate").forGetter(EffectData::rate))
                .apply(inst, EffectData::new));

        public MobEffectInstance build(MobEffect effect, float level) {
            return new MobEffectInstance(effect, this.duration.getInt(level), this.amplifier.getInt(level));
        }
    }

    public enum Target {
        ATTACK_SELF("attack_self"),
        ATTACK_TARGET("attack_target"),
        HURT_SELF("hurt_self"),
        HURT_ATTACKER("hurt_attacker"),
        BREAK_SELF("break_self"),
        ARROW_SELF("arrow_self"),
        ARROW_TARGET("arrow_target"),
        BLOCK_SELF("block_self"),
        BLOCK_ATTACKER("block_attacker");

        public static final Codec<Target> CODEC = PlaceboCodecs.enumCodec(Target.class);

        private final String id;

        Target(String id) {
            this.id = id;
        }

        public MutableComponent toComponent(Object... args) {
            return Component.translatable("affix.apotheosis_modern_ragnarok.rated_potion_affix.target." + this.id, args);
        }
    }
}
