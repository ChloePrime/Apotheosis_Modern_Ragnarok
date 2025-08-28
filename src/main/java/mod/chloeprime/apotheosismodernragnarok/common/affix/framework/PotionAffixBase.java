package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.RatedPotionAffix;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class PotionAffixBase extends AffixBaseUtility implements GunAffix {
    protected final Holder<MobEffect> effect;
    protected final Target target;
    protected final Map<LootRarity, EffectData> values;
    protected final Set<LootCategory> types;
    protected final boolean stackOnReapply;

    public PotionAffixBase(AffixDefinition def, Holder<MobEffect> effect, Target target, Map<LootRarity, EffectData> values, Set<LootCategory> types, boolean stackOnReapply) {
        super(def);
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
    public void doPostHurt(AffixInstance inst, LivingEntity user, DamageSource source) {
        if (this.target == Target.HURT_SELF) {
            this.applyEffect(user, user, inst.getRarity(), inst.level());
        }
        else if (this.target == Target.HURT_ATTACKER) {
            if (source.getEntity() instanceof LivingEntity tLiving) {
                this.applyEffect(user, tLiving, inst.getRarity(), inst.level());
            }
        }
    }

    @Override
    public void doPostAttack(AffixInstance inst, LivingEntity user, Entity target) {
        if (this.target == Target.ATTACK_SELF) {
            this.applyEffect(user, user, inst.getRarity(), inst.level());
        }
        else if (this.target == Target.ATTACK_TARGET) {
            if (target instanceof LivingEntity tLiving) {
                this.applyEffect(user, tLiving, inst.getRarity(), inst.level());
            }
        }
    }

    @Override
    public void onBlockBreak(AffixInstance inst, Player player, LevelAccessor world, BlockPos pos, BlockState state) {
        if (this.target == Target.BREAK_SELF) {
            this.applyEffect(player, player, inst.getRarity(), inst.level());
        }
    }

    @Override
    public void onProjectileImpact(float level, LootRarity rarity, Projectile proj, HitResult res, HitResult.Type type) {
        if (res instanceof EntityHitResult entityResult && entityResult.getEntity() instanceof LivingEntity victim) {
            switch (this.target) {
                case ARROW_SELF -> {
                    if (proj.getOwner() instanceof LivingEntity owner) {
                        this.applyEffect(owner, owner, rarity, level);
                    }
                }
                case ARROW_TARGET -> {
                    if (proj.getOwner() instanceof LivingEntity owner) {
                        this.applyEffect(owner, victim, rarity, level);
                    }
                }
                default -> {}
            }
        }
    }

    @Override
    public void onGunshotPost(ItemStack gun, AffixInstance instance, EntityHurtByGunEvent.Post event) {
        var isTargetCart = Optional.ofNullable(event.getHurtEntity())
                .map(Entity::getType)
                .map(et -> et.is(ModContent.Tags.GUN_IMMUNE))
                .orElse(false);
        if (isTargetCart) {
            return;
        }

        var owner = event.getAttacker();
        if (owner == null) {
            return;
        }

        if (this.target == Target.ARROW_SELF) {
            this.applyEffect(owner, owner, instance.rarity().get(), instance.level());
        } else if (this.target == Target.ARROW_TARGET) {
            if (event.getHurtEntity() instanceof LivingEntity victim) {
                this.applyEffect(owner, victim, instance.rarity().get(), instance.level());
            }
        }
    }

    @Override
    public void onGunshotKill(ItemStack gun, AffixInstance instance, EntityKillByGunEvent event) {
        var isTargetCart = Optional.ofNullable(event.getKilledEntity())
                .map(Entity::getType)
                .map(et -> et.is(ModContent.Tags.GUN_IMMUNE))
                .orElse(false);
        if (isTargetCart) {
            return;
        }

        var owner = event.getAttacker();
        if (owner == null) {
            return;
        }

        if (this.target == Target.ARROW_SELF) {
            this.applyEffect(owner, owner, instance.rarity().get(), instance.level());
        }
    }

    @Override
    public float onShieldBlock(AffixInstance inst, LivingEntity entity, DamageSource source, float amount) {
        if (this.target == Target.BLOCK_SELF) {
            this.applyEffect(entity, entity, inst.getRarity(), inst.level());
        }
        else if (this.target == Target.BLOCK_ATTACKER && source.getDirectEntity() instanceof LivingEntity victim) {
            this.applyEffect(entity, victim, inst.getRarity(), inst.level());
        }
        return amount;
    }

    public void applyEffect(LivingEntity owner, LivingEntity target, LootRarity rarity, float level) {
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
        startCooldown(this.id(), target);
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
        BLOCK_ATTACKER("block_attacker"),
        ADS_SELF("ads_self"),
        ADS_TARGET("ads_target");

        public static final Codec<Target> CODEC = PlaceboCodecs.enumCodec(Target.class);

        private final String id;

        Target(String id) {
            this.id = id;
        }

        @SuppressWarnings("unused")
        public static ExtensionInfo getExtensionInfo() {
            return ExtensionInfo.nonExtended(Target.class);
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
                        StepFunction.CODEC.optionalFieldOf("duration", ONE_SECOND).forGetter(EffectData::duration),
                        StepFunction.CODEC.fieldOf("amplifier").forGetter(EffectData::amplifier),
                        StepFunction.CODEC.optionalFieldOf("cooldown", ZERO).forGetter(EffectData::cooldown),
                        StepFunction.CODEC.optionalFieldOf("rate", ONE).forGetter(EffectData::rate))
                .apply(inst, PotionAffixBase.EffectData::new));

        public MobEffectInstance build(Holder<MobEffect> effect, float level) {
            return new MobEffectInstance(effect, this.duration.getInt(level), this.amplifier.getInt(level));
        }
    }
}
