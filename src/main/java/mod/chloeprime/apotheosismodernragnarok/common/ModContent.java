package mod.chloeprime.apotheosismodernragnarok.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.shadowsoffire.apotheosis.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.client.ClientConfig;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummyCoefficientAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummySpecialAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummyValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.*;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.*;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.component.FusedMultiplyAddFormula;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.component.NashornJavascriptValue;
import mod.chloeprime.apotheosismodernragnarok.common.gem.content.BloodBulletBonus;
import mod.chloeprime.apotheosismodernragnarok.common.gem.content.DictatorGemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.gem.content.PotionWhenShootBonus;
import mod.chloeprime.apotheosismodernragnarok.common.loot.ApothReforgeFunction;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FireDotEffect;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FreezeEffect;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.TyrannyEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.VerdantRuinEffect;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.MOD_ID;
import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;

/**
 * Affix types:
 * apotheosis_modern_ragnarok:bullet_saver  shots have rate to return the bullet
 * apotheosis_modern_ragnarok:armor_squash  shots have rate to destroy target's armor
 */
public class ModContent {
    public static final class Items {
        private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);
        public static final Supplier<Item> ANCIENT_MATERIAL = REGISTRY.register(
                "izanagi_object",
                () -> new SalvageItem(RarityRegistry.INSTANCE.holder(loc("ancient")), new Item.Properties())
        );

        private Items() {}
    }

    public static final class LootCategories extends ExtraLootCategories {
        private LootCategories() {}
    }

    public static final class Affix {
        public static final DynamicHolder<ArmorSquashAffix> ARMOR_SQUASH = holder("all_gun/special/armor_squash");
        public static final DynamicHolder<BulletSaverAffix>         BULLET_SAVER = holder("all_gun/special/frugality");
        public static final DynamicHolder<ExplosionOnHeadshotAffix> HEAD_EXPLODE = holder("all_gun/special/head_explode");
        public static final DynamicHolder<MagicalShotAffix>         MAGICAL_SHOT = holder("all_gun/special/magical_shot");
//        public static final DynamicHolder<DummyCoefficientAffix>    SPECTRAL_BULLET = holder("all_gun/special/spectral");

        @SuppressWarnings("unchecked")
        private static <T extends dev.shadowsoffire.apotheosis.affix.Affix> DynamicHolder<T> holder(String path) {
            return (DynamicHolder<T>) AffixRegistry.INSTANCE.holder(ApotheosisModernRagnarok.loc(path));
        }

        private Affix() {}
    }

    public static final class MobEffects {
        private static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MOD_ID);
        public static final DeferredHolder<MobEffect, FireDotEffect> FIRE_DOT = REGISTRY.register("fire_dot", FireDotEffect::create);
        public static final DeferredHolder<MobEffect, FreezeEffect> FREEZE = REGISTRY.register("freeze", FreezeEffect::create);
        public static final DeferredHolder<MobEffect, TyrannyEffect> TYRANNY = REGISTRY.register("tyranny", TyrannyEffect::create);
        public static final DeferredHolder<MobEffect, VerdantRuinEffect> VERDANT_RUIN = REGISTRY.register("verdant_ruin", VerdantRuinEffect::create);

        private MobEffects() {
        }
    }

    public static final class Tags {
        public static final TagKey<EntityType<?>> GUN_IMMUNE = TagKey.create(Registries.ENTITY_TYPE, loc("gun_immune"));
    }

    public static final class DamageTypes {
        public static final ResourceKey<DamageType> BULLET_ICE = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_ice"));
        public static final ResourceKey<DamageType> BULLET_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_fire"));
        public static final ResourceKey<DamageType> BULLET_IAF = ResourceKey.create(Registries.DAMAGE_TYPE, loc("bullet_iceandfire"));
        public static final ResourceKey<DamageType> PLAYER_ARMOR_PIERCING_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, loc("player_armor_piercing_attack"));
        public static final ResourceKey<DamageType> MOB_ARMOR_PIERCING_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, loc("mob_armor_piercing_attack"));
    }

    public static final class Particles {
        private static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MOD_ID);
        public static final Supplier<SimpleParticleType> BLOOD = REGISTRY.register("blood", () -> new SimpleParticleType(true));
    }

    public static final class Sounds {
        private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MOD_ID);
        public static final DeferredHolder<SoundEvent, SoundEvent> ARMOR_CRACK = registerSound("affix.armor_break");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_SHOTGUN = registerSound("affix.magical_shot.shotgun");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_SEMIAUTO = registerSound("affix.magical_shot.semi_auto");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_FULLAUTO = registerSound("affix.magical_shot.full_auto");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_BOLT_ACTION = registerSound("affix.magical_shot.bolt_action");
        public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_FIREBALL = registerSound("affix.magical.fireball");
        public static final DeferredHolder<SoundEvent, SoundEvent> HEAD_EXPLOSION = registerSound("affix.head_explosion");
        public static final DeferredHolder<SoundEvent, SoundEvent> CRITICAL_HIT = registerSound("critical_hit");
        public static final DeferredHolder<SoundEvent, SoundEvent> EXECUTION = registerSound("execution");
        public static final DeferredHolder<SoundEvent, SoundEvent> PERFECT_BLOCK = registerSound("perfect_block");
        public static final DeferredHolder<SoundEvent, SoundEvent> POSTURE_BREAK = registerSound("perfect_block_neutralized_target");

        private Sounds() {}
    }

    /**
     * @see GunEnchantmentHooks#canGunApplyEnchantmentAtTable(ItemStack, Holder, BooleanConsumer) 几个附魔 tag 的具体实现
     */
    public static final class Enchantments {
        public static final TagKey<Enchantment> AVAILABLE_FOR_GUNS = TagKey.create(Registries.ENCHANTMENT, loc("available_for_guns"));
        public static final TagKey<Enchantment> CAT_PISTOL = TagKey.create(Registries.ENCHANTMENT, loc("pistol"));
        public static final TagKey<Enchantment> CAT_SNIPER = TagKey.create(Registries.ENCHANTMENT, loc("sniper"));
        public static final TagKey<Enchantment> CAT_RIFLE = TagKey.create(Registries.ENCHANTMENT, loc("rifle"));
        public static final TagKey<Enchantment> CAT_SHOTGUN = TagKey.create(Registries.ENCHANTMENT, loc("shotgun"));
        public static final TagKey<Enchantment> CAT_SMG = TagKey.create(Registries.ENCHANTMENT, loc("smg"));
        public static final TagKey<Enchantment> CAT_HEAVY_WEAPON = TagKey.create(Registries.ENCHANTMENT, loc("heavy_weapon"));
        public static final TagKey<Enchantment> CAT_MACHINE_GUN = TagKey.create(Registries.ENCHANTMENT, loc("machine_gun"));
        public static final TagKey<Enchantment> CAT_MELEE_CAPABLE = TagKey.create(Registries.ENCHANTMENT, loc("melee_capable"));

        /**
         * 所有非背包供弹武器
         * @since 4.0.0
         */
        public static final TagKey<Enchantment> CAT_HAS_MAGAZINE = TagKey.create(Registries.ENCHANTMENT, loc("requires_magazine"));

        /**
         * 开启供弹系附魔互相冲突的 tag
         */
        public static final TagKey<Enchantment> BULLET_REGENERATION_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, loc("bullet_regeneration_exclusive"));

        public static final ResourceKey<Enchantment> STABILITY = ResourceKey.create(Registries.ENCHANTMENT, loc("stability"));
        public static final ResourceKey<Enchantment> EMERGENCY_PROTECTOR = ResourceKey.create(Registries.ENCHANTMENT, loc("emergency_protector"));
        public static final ResourceKey<Enchantment> RIPTIDE_WARHEAD = ResourceKey.create(Registries.ENCHANTMENT, loc("riptide_warhead"));
        public static final ResourceKey<Enchantment> SURVIVAL_INSTINCT = ResourceKey.create(Registries.ENCHANTMENT, loc("survival_instinct"));
        public static final ResourceKey<Enchantment> LAST_STAND = ResourceKey.create(Registries.ENCHANTMENT, loc("last_stand"));
        public static final ResourceKey<Enchantment> PROJECTION_MAGIC = ResourceKey.create(Registries.ENCHANTMENT, loc("projection_magic"));
        public static final ResourceKey<Enchantment> PERFECT_BLOCK = ResourceKey.create(Registries.ENCHANTMENT, loc("perfect_block"));
    }

    public static final class LootFunctions {
        private static final DeferredRegister<LootItemFunctionType<?>> REGISTRY = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MOD_ID);
        public static final Supplier<LootItemFunctionType<ApothReforgeFunction>> APOTH_REFORGE = REGISTRY.register("apoth_reforge", () -> new LootItemFunctionType<>(ApothReforgeFunction.CODEC));
    }

    @SuppressWarnings("deprecation")
    public static void setup() {
        ExtraLootCategories.init();
        AffixRegistry.INSTANCE.registerCodec(loc("bullet_saver"), BulletSaverAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("armor_squash"), ArmorSquashAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("explode_on_headshot"), ExplosionOnHeadshotAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("mob_effect_rated"), RatedPotionAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("mob_effect_ads"), AdsPotionAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("magical_shot"), MagicalShotAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("conditional_attribute"), ConditionalAttributeAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_valued"), DummyValuedAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_coefficient"), DummyCoefficientAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("dummy_special"), DummySpecialAffix.CODEC);
        GemBonus.CODEC.register(PotionWhenShootBonus.ID, PotionWhenShootBonus.CODEC);
        GemBonus.CODEC.register(BloodBulletBonus.ID, BloodBulletBonus.CODEC);
        GemBonus.CODEC.register(DictatorGemBonus.ID, DictatorGemBonus.CODEC);
        // 已不再使用
        AffixRegistry.INSTANCE.registerCodec(loc("magazine_capacity_conditional_attribute"), ConditionalAttributeAffix.CODEC_WITH_OLD_NAME);
    }

    public static void init0(IEventBus bus) {
        Items.REGISTRY.register(bus);
        MobEffects.REGISTRY.register(bus);
        Particles.REGISTRY.register(bus);
        Sounds.REGISTRY.register(bus);
        // 1.21.1+ Begin
        SinceMC1211.init(bus);
    }

    public static void init1(ModContainer context) {
        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String path) {
        return Sounds.REGISTRY.register(path, () -> SoundEvent.createVariableRangeEvent(ApotheosisModernRagnarok.loc(path)));
    }

    private ModContent() {}

    public static class SinceMC1211 {
        public static class DataAttachments {
            private static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

            public static final Supplier<AttachmentType<Long>> PERFECT_BLOCK_END_TIME = REGISTRY.register("perfect_block_end_time", DataAttachments::createTimestamp);
            public static final Supplier<AttachmentType<Double>> POSTURE = REGISTRY.register("posture", () -> DataAttachments.createDouble(0));
            public static final Supplier<AttachmentType<Long>> POSTURE_RECOVER_START_TIME = REGISTRY.register("posture_recover_start_time", DataAttachments::createTimestamp);

            public static final Supplier<AttachmentType<Boolean>> IS_BLOODY = REGISTRY.register("bloody", () -> AttachmentType
                    .builder(() -> false)
                    .serialize(Codec.BOOL).sync(ByteBufCodecs.BOOL)
                    .build());
            public static final Supplier<AttachmentType<Boolean>> IS_MAGICY = REGISTRY.register("magicy", () -> AttachmentType
                    .builder(() -> false)
                    .serialize(Codec.BOOL)
                    .build());

            public static final Supplier<AttachmentType<Double>> BULLET_UNDERWATER_FRICTION_FACTOR = REGISTRY.register("bullet_underwater_friction_factor", () -> DataAttachments.createDouble(1));

            private static AttachmentType<Long> createTimestamp() {
                return AttachmentType
                        .builder(() -> 0L)
                        .serialize(Codec.LONG).sync(ByteBufCodecs.VAR_LONG)
                        .build();
            }
            private static AttachmentType<Double> createDouble(double defaultValue) {
                return AttachmentType
                        .builder(() -> defaultValue)
                        .serialize(Codec.DOUBLE).sync(ByteBufCodecs.DOUBLE)
                        .build();
            }
        }

        public static class EnchantmentEffectComponents {
            private static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, MOD_ID);
            public static final Supplier<DataComponentType<List<EnchantmentAttributeEffect>>> ATTRIBUTES_WHEN_AMMO_EMPTY = REGISTRY.register(
                    "attributes_when_ammo_empty",
                    () -> DataComponentType
                            .<List<EnchantmentAttributeEffect>>builder()
                            .persistent(EnchantmentAttributeEffect.CODEC.codec().listOf())
                            .build()
            );

            public static final Supplier<DataComponentType<EnchantmentValueEffect>> PROJECTION_MAGIC_DELAY = REGISTRY.register(
                    "projection_magic_delay",
                    EnchantmentEffectComponents::value
            );

            public static final Supplier<DataComponentType<EnchantmentValueEffect>> BULLET_UNDERWATER_FRICTION = REGISTRY.register(
                    "bullet_underwater_friction",
                    EnchantmentEffectComponents::value
            );

            public static final Supplier<DataComponentType<EnchantmentValueEffect>> BULLET_DROP_RATE = REGISTRY.register(
                    "bullet_drop_rate",
                    EnchantmentEffectComponents::value
            );

            public static final Supplier<DataComponentType<EnchantmentValueEffect>> PERFECT_BLOCK_TIME_WINDOW = REGISTRY.register(
                    "perfect_block_time_window",
                    EnchantmentEffectComponents::value
            );


            private static DataComponentType<EnchantmentValueEffect> value() {
                return DataComponentType
                        .<EnchantmentValueEffect>builder()
                        .persistent(EnchantmentValueEffect.CODEC)
                        .build();
            }
        }

        @SuppressWarnings("unused")
        public static class EnchantmentValueTypes {
            private static final DeferredRegister<MapCodec<? extends LevelBasedValue>> REGISTRY = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, MOD_ID);
            public static final Supplier<MapCodec<FusedMultiplyAddFormula>> FUSED_MULTIPLY_ADD = REGISTRY.register(
                    "fused_multiply_add", () -> FusedMultiplyAddFormula.CODEC
            );            public static final Supplier<MapCodec<NashornJavascriptValue>> NASHORN_JAVASCRIPT = REGISTRY.register(
                    "nashorn_javascript", () -> NashornJavascriptValue.CODEC
            );
        }

        public static void init(IEventBus bus) {
            LootFunctions.REGISTRY.register(bus);
            DataAttachments.REGISTRY.register(bus);
            EnchantmentEffectComponents.REGISTRY.register(bus);
            EnchantmentValueTypes.REGISTRY.register(bus);
            ExtraLootCategories.DFR.register(bus);
        }
    }
}
