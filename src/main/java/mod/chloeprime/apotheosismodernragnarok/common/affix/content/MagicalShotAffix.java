package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AbstractAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummySpecialAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.GunAffix;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

import static com.tacz.guns.entity.EntityKineticBullet.TRACER_COLOR_OVERRIDER_KEY;
import static com.tacz.guns.entity.EntityKineticBullet.TRACER_SIZE_OVERRIDER_KEY;
import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;
import static net.minecraft.tags.DamageTypeTags.*;

@Mod.EventBusSubscriber
public class MagicalShotAffix extends DummySpecialAffix implements GunAffix {
    public static final DynamicHolder<MagicalShotAffix> AFFIX = ModContent.Affix.MAGICAL_SHOT;

    /**
     * 影响伤害类型
     */
    public static final String PDATA_KEY = loc("is_magical_shot").toString();

    /**
     * 影响曳光弹特效
     */
    public static final String PDATA_KEY_CLIENT_IS_MAGIC = loc("is_magical_shot_client").toString();

    @SuppressWarnings("deprecation")
    public static final Codec<Map<LootCategory, Holder<SoundEvent>>> SOUND_CODEC = Codec.unboundedMap(
            LootCategory.CODEC, BuiltInRegistries.SOUND_EVENT.holderByNameCodec()
    );

    public static final Codec<MagicalShotAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    SOUND_CODEC.fieldOf("sounds").forGetter(a -> a.sounds),
                    LootRarity.CODEC.fieldOf("min_rarity").forGetter(a -> a.minRarity))
            .apply(inst, MagicalShotAffix::new));

    private final Map<LootCategory, Holder<SoundEvent>> sounds;

    public Map<LootCategory, Holder<SoundEvent>> getSounds() {
        return Collections.unmodifiableMap(sounds);
    }

    public Optional<SoundEvent> getSoundFor(LootCategory category) {
        var holder = sounds.get(category);
        return holder.isBound() ? Optional.of(holder.get()) : Optional.empty();
    }

    public static Optional<SoundEvent> getSoundFor(ItemStack gun) {
        return Optional.ofNullable(AffixHelper.getAffixes(gun).get(AFFIX))
                .flatMap(instance -> AFFIX.get().getSoundFor(LootCategory.forItem(gun)));
    }

    public static boolean isMagicGun(ItemStack gun) {
        return AffixHelper.getAffixes(gun).containsKey(AFFIX);
    }

    @Override
    public void onBulletCreated(ItemStack gun, AffixInstance instance, BulletCreateEvent event) {
        event.getBullet().getPersistentData().putBoolean(PDATA_KEY, true);
    }

    @SubscribeEvent
    public static void preHurt(EntityHurtByGunEvent.Pre event) {
        if (!event.getBullet().getPersistentData().getBoolean(PDATA_KEY)) {
            return;
        }
        var origianlDamageSource = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        if (origianlDamageSource.is(BYPASSES_INVULNERABILITY)) {
            return;
        }
        @SuppressWarnings("DataFlowIssue")
        var newDamageSource = event.getBullet().damageSources().indirectMagic(origianlDamageSource.getDirectEntity(), origianlDamageSource.getEntity());
        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING, newDamageSource);
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING, newDamageSource);
    }

    @SubscribeEvent
    public static void onClientBulletCreated(BulletCreateEvent event) {
        if (!event.getBullet().level().isClientSide) {
            return;
        }
        if (isMagicGun(event.getGun())) {
            event.getBullet().getPersistentData().putBoolean(PDATA_KEY_CLIENT_IS_MAGIC, true);
            event.getBullet().getPersistentData().putIntArray(TRACER_COLOR_OVERRIDER_KEY, new int[]{102, 204, 204, 255});
            event.getBullet().getPersistentData().putFloat(TRACER_SIZE_OVERRIDER_KEY, 3);
        }
    }

    public static boolean clientIsMagicBullet(Projectile bullet) {
        return bullet.getPersistentData().contains(PDATA_KEY_CLIENT_IS_MAGIC, Tag.TAG_BYTE);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

    public MagicalShotAffix(Set<LootCategory> categories, Map<LootCategory, Holder<SoundEvent>> sounds, LootRarity minRarity) {
        super(AffixType.ABILITY, categories, minRarity);
        this.sounds = sounds;
    }
}
