package mod.chloeprime.apotheosismodernragnarok.common.gem.content;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.GunShootEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.PotionBonus;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.util.SocketHelper2;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Map;

@Mod.EventBusSubscriber
public class PotionWhenShootBonus extends GemBonus {
    public static Codec<PotionBonus.EffectData> EFFECT_DATA_CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.INT.fieldOf("duration").forGetter(PotionBonus.EffectData::duration),
                    Codec.INT.fieldOf("amplifier").forGetter(PotionBonus.EffectData::amplifier),
                    PlaceboCodecs.nullableField(Codec.INT, "cooldown", 0).forGetter(PotionBonus.EffectData::cooldown))
            .apply(inst, PotionBonus.EffectData::new));

    public static final Codec<PotionWhenShootBonus> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    gemClass(),
                    ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("mob_effect").forGetter(a -> a.effect),
                    LootRarity.mapCodec(EFFECT_DATA_CODEC).fieldOf("values").forGetter(a -> a.values),
                    PlaceboCodecs.nullableField(Codec.BOOL, "stack_on_reapply", false).forGetter(a -> a.stackOnReapply),
                    LootRarity.mapCodec(Codec.INT).fieldOf("max_level").forGetter(a -> a.maxLevel),
                    Codec.STRING.optionalFieldOf("custom_description", null).forGetter(a -> a.customDescription))
            .apply(inst, PotionWhenShootBonus::new));

    protected final MobEffect effect;
    protected final Map<LootRarity, PotionBonus.EffectData> values;
    protected final boolean stackOnReapply;
    protected final Map<LootRarity, Integer> maxLevel;
    protected final @Nullable String customDescription;

    public PotionWhenShootBonus(
            GemClass gemClass,
            MobEffect effect,
            Map<LootRarity, PotionBonus.EffectData> values,
            boolean stackOnReapply,
            Map<LootRarity, Integer> maxLevel,
            @Nullable String customDescription
    ) {
        super(ApotheosisModernRagnarok.loc("mob_effect_when_shoot"), gemClass);
        this.effect = effect;
        this.values = values;
        this.stackOnReapply = stackOnReapply;
        this.maxLevel = maxLevel;
        this.customDescription = customDescription;
    }

    @SubscribeEvent
    public static void onLivingShoot(GunShootEvent event) {
        SocketHelper2.streamGemBonuses(event.getGunItemStack())
                .map(pair -> Pair.of(pair.getKey() instanceof PotionWhenShootBonus bonus ? bonus : null, pair.getValue()))
                .filter(pair -> pair.getKey() != null)
                .forEach(pair -> {
                    var bonus = pair.getKey();
                    var instance = pair.getValue();
                    bonus.applyEffect(instance.gemStack(), event.getShooter(), instance.rarity().get());
                });
    }

    public int getCooldown(LootRarity rarity) {
        PotionBonus.EffectData data = this.values.get(rarity);
        return data.cooldown();
    }

    public void applyEffect(ItemStack gemStack, LivingEntity target, LootRarity rarity) {
        int cooldown = this.getCooldown(rarity);
        if (cooldown != 0 && Affix.isOnCooldown(this.getCooldownId(gemStack), cooldown, target)) return;
        PotionBonus.EffectData data = this.values.get(rarity);
        var inst = target.getEffect(this.effect);
        if (this.stackOnReapply && inst != null) {
            int amplifier = Math.min(maxLevel.get(rarity) - 1, inst.getAmplifier() + 1 + data.amplifier());
            var newInst = new MobEffectInstance(this.effect, Math.max(inst.getDuration(), data.duration()), amplifier);
            target.addEffect(newInst);
        } else {
            target.addEffect(data.build(this.effect));
        }
        Affix.startCooldown(this.getCooldownId(gemStack), target);
    }

    @Override
    public PotionWhenShootBonus validate() {
        Preconditions.checkNotNull(this.effect, "Null mob effect");
        Preconditions.checkNotNull(this.values, "Null values map");
        Preconditions.checkNotNull(this.maxLevel, "Null max level");
        return this;
    }

    @Override
    public Component getSocketBonusTooltip(ItemStack gem, LootRarity rarity) {
        if (customDescription != null) {
            return Component.translatable(customDescription, maxLevel.get(rarity)).withStyle(ChatFormatting.YELLOW);
        }
        // 以下代码不应该被执行
        var comp = Component.literal("");
        int cooldown = this.getCooldown(rarity);
        if (cooldown != 0) {
            Component cd = Component.translatable("affix.apotheosis.cooldown", StringUtil.formatTickDuration(cooldown));
            comp = comp.append(" ").append(cd);
        }
        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        return comp;
    }

    @Override
    public int getNumberOfUUIDs() {
        return 0;
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    @Override
    public boolean supports(LootRarity rarity) {
        return this.values.containsKey(rarity);
    }
}
