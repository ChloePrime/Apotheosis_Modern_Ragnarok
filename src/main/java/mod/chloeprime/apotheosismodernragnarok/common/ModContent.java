package mod.chloeprime.apotheosismodernragnarok.common;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummyCoefficientAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummySpecialAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummyValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.*;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FireDotEffect;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FreezeEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.MOD_ID;
import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;

/**
 * Affix types:
 * apotheosis_modern_ragnarok:bullet_saver  shots have rate to return the bullet
 * apotheosis_modern_ragnarok:armor_squash  shots have rate to destroy target's armor
 */
public class ModContent {
    public static class Items {
        private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
        public static final RegistryObject<Item> ANCIENT_MATERIAL = REGISTRY.register(
                "izanagi_object",
                () -> new SalvageItem(RarityRegistry.INSTANCE.holder(Apotheosis.loc("ancient")), new Item.Properties())
        );

        private Items() {}
    }

    public static class LootCategories extends ExtraLootCategories {
        private LootCategories() {}
    }

    public static class Affix {
        public static final DynamicHolder<ArmorSquashAffix>         ARMOR_SQUASH = holder("all_gun/special/armor_squash");
        public static final DynamicHolder<BulletSaverAffix>         BULLET_SAVER = holder("all_gun/special/frugality");
        public static final DynamicHolder<ExplosionOnHeadshotAffix> HEAD_EXPLODE = holder("all_gun/special/head_explode");
        public static final DynamicHolder<MagicalShotAffix>         MAGICAL_SHOT = holder("all_gun/special/magical_shot");
//        public static final DynamicHolder<DummyCoefficientAffix>    SPECTRAL_BULLET = holder("all_gun/special/spectral");

        private static <T extends dev.shadowsoffire.apotheosis.adventure.affix.Affix> DynamicHolder<T> holder(String path) {
            return AffixRegistry.INSTANCE.holder(ApotheosisModernRagnarok.loc(path));
        }

        private Affix() {}
    }

    public static class MobEffects {
        private static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);
        public static final RegistryObject<FireDotEffect> FIRE_DOT = REGISTRY.register("fire_dot", FireDotEffect::create);
        public static final RegistryObject<FreezeEffect> FREEZE = REGISTRY.register("freeze", FreezeEffect::create);

        private MobEffects() {
        }
    }

    public static class DamageTypes {
        public static final ResourceKey<DamageType> BULLET_ICE = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_ice"));
        public static final ResourceKey<DamageType> BULLET_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_fire"));
        public static final ResourceKey<DamageType> BULLET_IAF = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_iceandfire"));
    }

    public static class Sounds {
        private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
        public static final RegistryObject<SoundEvent> ARMOR_CRACK = registerSound("affix.armor_break");
        public static final RegistryObject<SoundEvent> MAGIC_SHOTGUN = registerSound("affix.magical_shot.shotgun");
        public static final RegistryObject<SoundEvent> MAGIC_SEMIAUTO = registerSound("affix.magical_shot.semi_auto");
        public static final RegistryObject<SoundEvent> MAGIC_FULLAUTO = registerSound("affix.magical_shot.full_auto");
        public static final RegistryObject<SoundEvent> MAGIC_BOLT_ACTION = registerSound("affix.magical_shot.bolt_action");
        public static final RegistryObject<SoundEvent> MAGIC_FIREBALL = registerSound("affix.magical.fireball");
        public static final RegistryObject<SoundEvent> HEAD_EXPLOSION = registerSound("affix.head_explosion");
        public static final RegistryObject<SoundEvent> CRITICAL_HIT = registerSound("critical_hit");

        private Sounds() {}
    }

    public static void setup() {
        ExtraLootCategories.init();
        AffixRegistry.INSTANCE.registerCodec(loc("bullet_saver"), BulletSaverAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("armor_squash"), ArmorSquashAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("explode_on_headshot"), ExplosionOnHeadshotAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("mob_effect_rated"), RatedPotionAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("mob_effect_ads"), AdsPotionAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("magical_shot"), MagicalShotAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_valued"), DummyValuedAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_coefficient"), DummyCoefficientAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_special"), DummySpecialAffix.CODEC);
    }

    public static void init0(IEventBus bus) {
        Items.REGISTRY.register(bus);
        MobEffects.REGISTRY.register(bus);
        Sounds.REGISTRY.register(bus);
    }

    public static void init1(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private static RegistryObject<SoundEvent> registerSound(String path) {
        return Sounds.REGISTRY.register(path, () -> SoundEvent.createVariableRangeEvent(ApotheosisModernRagnarok.loc(path)));
    }

    private ModContent() {}
}
