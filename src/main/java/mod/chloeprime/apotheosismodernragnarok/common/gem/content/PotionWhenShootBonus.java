package mod.chloeprime.apotheosismodernragnarok.common.gem.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.mixin.LivingEntityInvoker;
import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.MobEffectBonus;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AffixBaseUtility;
import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GunGemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.util.SocketHelper2;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Map;

@EventBusSubscriber
public class PotionWhenShootBonus extends GemBonus implements GunGemBonus {
    public enum When {
        SHOOT,
        HEADSHOT
    }

    public static final ResourceLocation ID = ApotheosisModernRagnarok.loc("mob_effect_when_shoot");

    public static Codec<MobEffectBonus.EffectData> EFFECT_DATA_CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.INT.fieldOf("duration").forGetter(MobEffectBonus.EffectData::duration),
                    Codec.INT.fieldOf("amplifier").forGetter(MobEffectBonus.EffectData::amplifier),
                    Codec.INT.optionalFieldOf("cooldown", 0).forGetter(MobEffectBonus.EffectData::cooldown))
            .apply(inst, MobEffectBonus.EffectData::new));

    public static final Codec<PotionWhenShootBonus> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    gemClass(),
                    BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("mob_effect").forGetter(a -> a.effect),
                    Purity.mapCodec(EFFECT_DATA_CODEC).fieldOf("values").forGetter(a -> a.values),
                    Codec.BOOL.optionalFieldOf("stack_on_reapply", false).forGetter(a -> a.stackOnReapply),
                    Purity.mapCodec(Codec.INT).fieldOf("max_level").forGetter(a -> a.maxLevel),
                    PlaceboCodecs.enumCodec(When.class).optionalFieldOf("when", When.SHOOT).forGetter(a -> a.when),
                    Codec.STRING.optionalFieldOf("custom_description", null).forGetter(a -> a.customDescription))
            .apply(inst, PotionWhenShootBonus::new));

    protected final Holder<MobEffect> effect;
    protected final Map<Purity, MobEffectBonus.EffectData> values;
    protected final boolean stackOnReapply;
    protected final Map<Purity, Integer> maxLevel;
    protected final When when;
    protected final @Nullable String customDescription;

    public PotionWhenShootBonus(
            GemClass gemClass,
            Holder<MobEffect> effect,
            Map<Purity, MobEffectBonus.EffectData> values,
            boolean stackOnReapply,
            Map<Purity, Integer> maxLevel,
            When when,
            @Nullable String customDescription
    ) {
        super(gemClass);
        this.effect = effect;
        this.values = values;
        this.stackOnReapply = stackOnReapply;
        this.maxLevel = maxLevel;
        this.when = when;
        this.customDescription = customDescription;
    }

    @Override
    public void onGunshotPost(ItemStack gun, ItemStack gem, GemInstance instance, EntityHurtByGunEvent.Post event) {
        onGunshotPost(instance, event.getAttacker(), event.isHeadShot());
    }

    @Override
    public void onGunshotKill(ItemStack gun, ItemStack gem, GemInstance instance, EntityKillByGunEvent event) {
        onGunshotPost(instance, event.getAttacker(), event.isHeadShot());
    }

    private void onGunshotPost(GemInstance instance, @Nullable LivingEntity shooter, boolean isHeadshot) {
        if (shooter == null) {
            return;
        }
        if (when == When.HEADSHOT && isHeadshot) {
            applyEffect(instance, shooter);
        }
    }

    @SubscribeEvent
    public static void onLivingShoot(GunShootEvent event) {
        SocketHelper2.streamGemBonuses(event.getGunItemStack())
                .map(pair -> Pair.of((pair.getKey() instanceof PotionWhenShootBonus bonus && bonus.when == When.SHOOT) ? bonus : null, pair.getValue()))
                .filter(pair -> pair.getKey() != null)
                .forEach(pair -> {
                    var bonus = pair.getKey();
                    var instance = pair.getValue();
                    bonus.applyEffect(instance, event.getShooter());
                });
    }

    public int getCooldown(Purity rarity) {
        MobEffectBonus.EffectData data = this.values.get(rarity);
        return data.cooldown();
    }

    public void applyEffect(GemInstance inst, LivingEntity target) {
        int cooldown = this.getCooldown(inst.purity());
        if (cooldown != 0 && Affix.isOnCooldown(makeUniqueId(inst), cooldown, target)) {
            return;
        }
        MobEffectBonus.EffectData data = this.values.get(inst.purity());
        MobEffectInstance effectInst = target.getEffect(this.effect);
        if (this.stackOnReapply && effectInst != null) {
            int duration = Math.max(effectInst.getDuration(), data.duration());
            int amp = Math.min(this.maxLevel.get(inst.purity()) - 1, effectInst.getAmplifier() + 1 + data.amplifier());
            var newInst = new MobEffectInstance(this.effect, duration, amp, effectInst.isAmbient(), effectInst.isVisible());
            effectInst.update(newInst);
            ((LivingEntityInvoker) target).callOnEffectUpdated(effectInst, true, null);
            effectInst.onEffectStarted(target);
        }
        else {
            target.addEffect(data.build(this.effect));
        }
        Affix.startCooldown(makeUniqueId(inst), target);
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        if (customDescription != null) {
            return Component.translatable(customDescription, maxLevel.get(gem.purity())).withStyle(AffixBaseUtility.BRIGHT_RED);
        }
        var rarity = gem.purity();
        // 以下代码不应该被执行
        var comp = Component.literal("");
        int cooldown = this.getCooldown(rarity);
        if (cooldown != 0) {
            Component cd = Component.translatable("affix.apotheosis.cooldown", StringUtil.formatTickDuration(cooldown, ctx.tickRate()));
            comp = comp.append(" ").append(cd);
        }
        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        return comp;
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    @Override
    public boolean supports(Purity rarity) {
        return this.values.containsKey(rarity);
    }
}
