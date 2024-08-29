package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.apotheosismodernragnarok.api.events.ArmorSquashAffixTakeEffectEvent;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.GunAffix;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * 命中时概率碎甲。
 * 在栓动大威力步枪上的碎甲概率大幅增加。
 * <p>
 * 类型名 apotheosis_modern_ragnarok:armor_squash
 * 实例名 apotheosis_modern_ragnarok:armor_squash
 * <p/>
 */
@Mod.EventBusSubscriber
public class ArmorSquashAffix extends AbstractValuedAffix implements GunAffix {

    public static final Codec<ArmorSquashAffix> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    GemBonus.VALUES_CODEC.fieldOf("values").forGetter(AbstractValuedAffix::getValues),
                    ExtraCodecs.COEFFICIENT_BY_CATEGORY.fieldOf("coefficients").forGetter(a -> a.coefficients))
            .apply(builder, ArmorSquashAffix::new));

    public ArmorSquashAffix(
            Set<LootCategory> categories,
            Map<LootRarity, StepFunction> values,
            Map<LootCategory, Double> coefficients) {
        super(AffixType.ABILITY, categories, values);
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
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        var percent = 100 * getValue(stack, rarity, level);
        list.accept(Component.translatable(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
    }

    public final double getCaliberBonus(ItemStack stack) {
        return getCoefficients().getOrDefault(LootCategory.forItem(stack), 1.0);
    }

    @Override
    public void onGunshotPost(ItemStack stack, AffixInstance instance, EntityHurtByGunEvent.Post event) {
        Optional.ofNullable(event.getHurtEntity()).ifPresent(victim -> {
            if (victim instanceof Player) {
                return;
            }
            Optional.ofNullable(event.getAttacker())
                    .ifPresent(attacker -> onLivingHurt0(victim, stack, instance, attacker));
        });
    }

    private void onLivingHurt0(Entity victim, ItemStack gun, AffixInstance instance, Entity attacker) {
        if (!(victim instanceof LivingEntity livingVictim)) {
            return;
        }
        // 概率检定
        if (livingVictim.getRandom().nextFloat() > getValue(gun, instance)) {
            return;
        }
        // 寻找一件护甲
        StreamSupport.stream(livingVictim.getArmorSlots().spliterator(), false)
                .filter(stack -> !stack.isEmpty())
                .findAny()
                .ifPresent(armor -> {
                    // 打碎护甲
                    onArmorBreak(livingVictim, attacker, this, armor);
                    armor.shrink(1);
                });
    }

    private final Map<LootCategory, Double> coefficients;

    private static void onArmorBreak(LivingEntity victim, Entity source, ArmorSquashAffix affix, ItemStack armor) {
        // runs on the server :)
        if (MinecraftForge.EVENT_BUS.post(new ArmorSquashAffixTakeEffectEvent(victim, source, affix, armor))) {
            return;
        }
        victim.level().playSound(null, victim, ModContent.Sounds.ARMOR_CRACK.get(), victim.getSoundSource(), 1, 1);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
