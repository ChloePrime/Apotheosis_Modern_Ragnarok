package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.google.gson.JsonObject;
import com.tac.guns.entity.DamageSourceProjectile;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.AbstractValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.GunCategories;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shadows.apotheosis.adventure.affix.AffixHelper;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.affix.AffixType;
import shadows.apotheosis.adventure.loot.LootRarity;
import shadows.placebo.json.DynamicRegistryObject;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * 命中时概率碎甲。
 * 在栓动大威力步枪上的碎甲概率大幅增加。
 * <p>
 * 类型名 apotheosis_modern_ragnarok:bullet_saver
 * 实例名 apotheosis_modern_ragnarok:frugality
 * <p/>
 * @see GunCategories.Tags#LARGE_CALIBER 大口径步枪的识别标签
 * @see #onLivingHurt(LivingHurtEvent) 实现
 */
@Mod.EventBusSubscriber
public class ArmorSquashAffix extends AbstractValuedAffix {
    public static final DynamicRegistryObject<ArmorSquashAffix> INSTANCE
            = AffixManager.INSTANCE.makeObj(ApotheosisModernRagnarok.loc("armor_squash"));

    public ArmorSquashAffix(Pojo data) {
        super(AffixType.EFFECT, data);
        this.largeCaliberBonus = data.largeCaliberBonus;
    }

    public float getLargeCaliberBonus() {
        return largeCaliberBonus;
    }

    @Override
    public float getValue(ItemStack gun, LootRarity rarity, float level) {
        return getCaliberBonus(gun) * super.getValue(gun, rarity, level);
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        var percent = 100 * getValue(stack, rarity, level);
        list.accept(new TranslatableComponent(desc(), fmt(percent)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
    }

    public final float getCaliberBonus(ItemStack stack) {
        return GunCategories.LARGE_CALIBER.test(stack) ? getLargeCaliberBonus() : 1;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent e) {
        INSTANCE.ifPresent(affix -> {
            if (e.getEntity().getLevel().isClientSide() || !(e.getSource() instanceof DamageSourceProjectile source)) {
                return;
            }
            DamageUtils.ifIsKeptDamage(source, e.getAmount(), fixedAmount -> {
                var victim = e.getEntityLiving();
                if (victim instanceof Player) {
                    return;
                }
                var gun = source.getWeapon();
                Optional.ofNullable(AffixHelper.getAffixes(gun).get(affix)).ifPresent(instance -> {
                    // 概率检定
                    if (victim.getRandom().nextFloat() <= affix.getValue(gun, instance)) {
                        // 寻找一件护甲
                        StreamSupport.stream(victim.getArmorSlots().spliterator(), false)
                                .filter(stack -> !stack.isEmpty())
                                .findAny()
                                .ifPresent(armor -> {
                                    // 打碎护甲
                                    onArmorBreak(victim, source, armor);
                                    armor.shrink(1);
                                });
                    }
                });
            });
        });
    }

    private final float largeCaliberBonus;

    public static class Pojo extends AbstractValuedAffix.Pojo {
        public float largeCaliberBonus;
    }

    private static void onArmorBreak(LivingEntity victim, DamageSource source, ItemStack armor) {
        // runs on the server :)
        victim.getLevel().playSound(null, victim, ModContent.Sounds.ARMOR_CRACK.get(), victim.getSoundSource(), 1, 1);
    }

    public static void readBase(JsonObject obj, Pojo dataHolder) {
        AbstractValuedAffix.readBase(obj, dataHolder);
        dataHolder.largeCaliberBonus = GsonHelper.getAsFloat(obj, "large_caliber_bonus", 1);
    }

    public static void readBase(FriendlyByteBuf buf, Pojo dataHolder) {
        AbstractValuedAffix.readBase(buf, dataHolder);
        dataHolder.largeCaliberBonus = buf.readFloat();
    }

    @SuppressWarnings("unused")
    public static ArmorSquashAffix read(JsonObject obj) {
        return read(obj, ArmorSquashAffix::new, Pojo::new, ArmorSquashAffix::readBase);
    }

    @SuppressWarnings("unused")
    public static ArmorSquashAffix read(FriendlyByteBuf buf) {
        return read(buf, ArmorSquashAffix::new, Pojo::new, ArmorSquashAffix::readBase);
    }

    @Override
    public JsonObject write() {
        return super.write();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeFloat(largeCaliberBonus);
    }
}
