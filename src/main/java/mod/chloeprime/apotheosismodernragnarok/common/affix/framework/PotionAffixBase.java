package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.RatedPotionAffix;
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
import net.minecraftforge.common.IExtensibleEnum;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class PotionAffixBase extends AffixBaseUtility implements GunAffix {
    protected final MobEffect effect;
    protected final Target target;
    protected final Map<LootRarity, EffectData> values;
    protected final Set<LootCategory> types;
    protected final boolean stackOnReapply;

    public PotionAffixBase(AffixType type, MobEffect effect, Target target, Map<LootRarity, EffectData> values, Set<LootCategory> types, boolean stackOnReapply) {
        super(type);
        this.effect = effect;
        this.target = target;
        this.values = values;
        this.types = types;
        this.stackOnReapply = stackOnReapply;
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
        } else if (this.target == Target.ARROW_TARGET) {
            if (type == HitResult.Type.ENTITY && ((EntityHitResult) res).getEntity() instanceof LivingEntity target) {
                this.applyEffect(target, rarity, level);
            }
        }
    }

    public void onGunshotPost(ItemStack gun, AffixInstance instance, EntityHurtByGunEvent.Post event) {
        if (this.target == Target.ARROW_SELF) {
            Optional.ofNullable(event.getAttacker()).ifPresent(owner -> this.applyEffect(owner, instance.rarity().get(), instance.level()));
        } else if (this.target == Target.ARROW_TARGET) {
            if (event.getHurtEntity() instanceof LivingEntity victim) {
                this.applyEffect(victim, instance.rarity().get(), instance.level());
            }
        }
    }

    @Override
    public void onGunshotKill(ItemStack gun, AffixInstance instance, EntityKillByGunEvent event) {
        if (this.target == Target.ARROW_SELF) {
            Optional.ofNullable(event.getAttacker()).ifPresent(owner -> this.applyEffect(owner, instance.rarity().get(), instance.level()));
        }
    }

    @Override
    public float onShieldBlock(ItemStack stack, LootRarity rarity, float level, LivingEntity entity, DamageSource source, float amount) {
        if (this.target == Target.BLOCK_SELF) {
            this.applyEffect(entity, rarity, level);
        } else if (this.target == Target.BLOCK_ATTACKER && source.getDirectEntity() instanceof LivingEntity target) {
            this.applyEffect(target, rarity, level);
        }
        return amount;
    }

    public void applyEffect(LivingEntity target, LootRarity rarity, float level) {
        if (target.level().isClientSide()) return;

        EffectData data = this.values.get(rarity);
        var inst = target.getEffect(this.effect);
        if (this.stackOnReapply && inst != null) {
            var newInst = new MobEffectInstance(this.effect, (int) Math.max(inst.getDuration(), data.duration().get(level)), (int) (inst.getAmplifier() + 1 + data.amplifier().get(level)));
            target.addEffect(newInst);
        }
        else {
            target.addEffect(data.build(this.effect, level));
        }
        startCooldown(this.getId(), target);
    }

    public enum Target implements IExtensibleEnum {
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

        public static Target create(String fieldName, String langKey) {
            throw new AssertionError("Enum not extended");
        }

        public MutableComponent toComponent(Object... args) {
            return Component.translatable("affix.apotheosis_modern_ragnarok.rated_potion_affix.target." + this.id, args);
        }
    }

    public record EffectData(StepFunction duration, StepFunction amplifier, StepFunction cooldown, StepFunction rate) {
        public static final StepFunction ZERO = StepFunction.constant(0);
        public static final StepFunction ONE = StepFunction.constant(1);
        public static final StepFunction ONE_SECOND = StepFunction.constant(20);

        public static final Codec<RatedPotionAffix.EffectData> CODEC = RecordCodecBuilder.create(inst -> inst
                .group(
                        PlaceboCodecs.nullableField(StepFunction.CODEC,"duration", ONE_SECOND).forGetter(EffectData::duration),
                        StepFunction.CODEC.fieldOf("amplifier").forGetter(EffectData::amplifier),
                        PlaceboCodecs.nullableField(StepFunction.CODEC,"cooldown", ZERO).forGetter(EffectData::cooldown),
                        PlaceboCodecs.nullableField(StepFunction.CODEC,"rate", ONE).forGetter(EffectData::rate))
                .apply(inst, PotionAffixBase.EffectData::new));

        public MobEffectInstance build(MobEffect effect, float level) {

            return new MobEffectInstance(effect, this.duration.getInt(level), this.amplifier.getInt(level));
        }
    }
}
