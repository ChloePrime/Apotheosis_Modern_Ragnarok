package mod.chloeprime.apotheosismodernragnarok.common;

import com.google.common.base.Predicates;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.client.ClientConfig;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummyCoefficientAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummySpecialAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.DummyValuedAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.*;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.*;
import mod.chloeprime.apotheosismodernragnarok.common.gem.content.BloodBulletBonus;
import mod.chloeprime.apotheosismodernragnarok.common.gem.content.DictatorGemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.gem.content.PotionWhenShootBonus;
import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GemInjectionRegistry;
import mod.chloeprime.apotheosismodernragnarok.common.loot.ApothReforgeFunction;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FireDotEffect;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.FreezeEffect;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.TyrannyEffect;
import mod.chloeprime.apotheosismodernragnarok.common.mob_effects.VerdantRuinEffect;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.MOD_ID;
import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;

/**
 * Affix types:
 * apotheosis_modern_ragnarok:bullet_saver  shots have rate to return the bullet
 * apotheosis_modern_ragnarok:armor_squash  shots have rate to destroy target's armor
 */
public class ModContent {
    public static final class Items {
        private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
        public static final RegistryObject<Item> ANCIENT_MATERIAL = REGISTRY.register(
                "izanagi_object",
                () -> new SalvageItem(RarityRegistry.INSTANCE.holder(Apotheosis.loc("ancient")), new Item.Properties())
        );

        private Items() {}
    }

    public static final class LootCategories extends ExtraLootCategories {
        private LootCategories() {}
    }

    public static final class Affix {
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

    public static final class Attributes {
        private static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MOD_ID);
        public static final RegistryObject<Attribute> POSTURE = createAttribute("posture", 0, 0, 1);
        private Attributes() {
        }
    }

    public static final class MobEffects {
        private static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);
        public static final RegistryObject<FireDotEffect> FIRE_DOT = REGISTRY.register("fire_dot", FireDotEffect::create);
        public static final RegistryObject<FreezeEffect> FREEZE = REGISTRY.register("freeze", FreezeEffect::create);
        public static final RegistryObject<TyrannyEffect> TYRANNY = REGISTRY.register("tyranny", TyrannyEffect::create);
        public static final RegistryObject<VerdantRuinEffect> VERDANT_RUIN = REGISTRY.register("verdant_ruin", VerdantRuinEffect::create);

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
        private static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);
        public static final RegistryObject<SimpleParticleType> BLOOD = REGISTRY.register("blood", () -> new SimpleParticleType(true));
    }

    public static final class Sounds {
        private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
        public static final RegistryObject<SoundEvent> ARMOR_CRACK = registerSound("affix.armor_break");
        public static final RegistryObject<SoundEvent> MAGIC_SHOTGUN = registerSound("affix.magical_shot.shotgun");
        public static final RegistryObject<SoundEvent> MAGIC_SEMIAUTO = registerSound("affix.magical_shot.semi_auto");
        public static final RegistryObject<SoundEvent> MAGIC_FULLAUTO = registerSound("affix.magical_shot.full_auto");
        public static final RegistryObject<SoundEvent> MAGIC_BOLT_ACTION = registerSound("affix.magical_shot.bolt_action");
        public static final RegistryObject<SoundEvent> MAGIC_FIREBALL = registerSound("affix.magical.fireball");
        public static final RegistryObject<SoundEvent> HEAD_EXPLOSION = registerSound("affix.head_explosion");
        public static final RegistryObject<SoundEvent> CRITICAL_HIT = registerSound("critical_hit");
        public static final RegistryObject<SoundEvent> EXECUTION = registerSound("execution");
        public static final RegistryObject<SoundEvent> PERFECT_BLOCK = registerSound("perfect_block");
        public static final RegistryObject<SoundEvent> POSTURE_BREAK = registerSound("perfect_block_neutralized_target");

        private Sounds() {}
    }

    /**
     * @see GunEnchantmentHooks#canGunApplyEnchantmentAtTable(Item, ItemStack, Enchantment, CallbackInfoReturnable) 几个 alwaysFalse 的附魔类型的具体实现
     */
    public static final class Enchantments {
        public static final EnchantmentCategory THE_CATEGORY = EnchantmentCategory.create("AMR_GUN_APOTH", IGun.class::isInstance);
        public static final EnchantmentCategory CAT_PISTOL = EnchantmentCategory.create("AMR_PISTOL_APOTH", Predicates.alwaysFalse());
        public static final EnchantmentCategory CAT_SNIPER = EnchantmentCategory.create("AMR_SNIPER_APOTH", Predicates.alwaysFalse());
        public static final EnchantmentCategory CAT_RIFLE = EnchantmentCategory.create("AMR_RIFLE_APOTH", Predicates.alwaysFalse());
        public static final EnchantmentCategory CAT_SHOTGUN = EnchantmentCategory.create("AMR_SHOTGUN_APOTH", Predicates.alwaysFalse());
        public static final EnchantmentCategory CAT_SMG = EnchantmentCategory.create("AMR_SMG_APOTH", Predicates.alwaysFalse());
        public static final EnchantmentCategory CAT_HEAVY_WEAPON = EnchantmentCategory.create("AMR_HEAVY_WEAPON_APOTH", Predicates.alwaysFalse());
        public static final EnchantmentCategory CAT_MACHINE_GUN = EnchantmentCategory.create("AMR_MACHINE_GUN_APOTH", Predicates.alwaysFalse());
        public static final EnchantmentCategory CAT_MELEE_CAPABLE = EnchantmentCategory.create("AMR_MACHINE_GUN_APOTH", Predicates.alwaysFalse());

        /**
         * 所有非背包供弹武器
         * @since 4.0.0
         */
        public static final EnchantmentCategory CAT_HAS_MAGAZINE = EnchantmentCategory.create("AMR_HAS_MAGAZINE_APOTH", Predicates.alwaysFalse());

        private static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);
        public static final RegistryObject<Enchantment> STABILITY = REGISTRY.register("stability", ReduceRecoilEnchantment::new);
        public static final RegistryObject<Enchantment> EMERGENCY_PROTECTOR = REGISTRY.register("emergency_protector", EmergencyProtectorEnchantment::new);
        public static final RegistryObject<Enchantment> RIPTIDE_WARHEAD = REGISTRY.register("riptide_warhead", BulletRiptideEnchantment::new);
        public static final RegistryObject<Enchantment> SURVIVAL_INSTINCT = REGISTRY.register("survival_instinct", SurvivalInstinctEnchantment::new);
        public static final RegistryObject<Enchantment> LAST_STAND = REGISTRY.register("last_stand", LastStandEnchantment::new);
        public static final RegistryObject<Enchantment> PROJECTION_MAGIC = REGISTRY.register("projection_magic", ProjectionMagicEnchantment::new);
        public static final RegistryObject<Enchantment> PERFECT_BLOCK = REGISTRY.register("perfect_block", PerfectBlockEnchantment::new);
    }

    public static final class LootFunctions {
        public static final LootItemFunctionType APOTH_REFORGE = register("apoth_reforge", new ApothReforgeFunction.Serializer());

        private LootFunctions() {
        }

        private static LootItemFunctionType register(String name, Serializer<? extends LootItemFunction> serializer) {
            return Registry.register(
                    BuiltInRegistries.LOOT_FUNCTION_TYPE,
                    new ResourceLocation(ApotheosisModernRagnarok.MOD_ID, name),
                    new LootItemFunctionType(serializer));
        }

        private static void init() {
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static RegistryObject<Attribute> createAttribute(String name, double defaultValue, double min, double max) {
        return createAttribute(name, defaultValue, min, max, (a) -> a.setSyncable(true));
    }

    private static RegistryObject<Attribute> createAttribute(String name, double defaultValue, double min, double max, Consumer<Attribute> customizer) {
        return Attributes.REGISTRY.register(name, () -> {
            RangedAttribute attribute = new RangedAttribute(createAttributeLangKey(name), defaultValue, min, max);
            customizer.accept(attribute);
            return attribute;
        });
    }

    private static String createAttributeLangKey(String name) {
        return "attribute.name.%s.%s".formatted(MOD_ID, name);
    }

    @SuppressWarnings("deprecation")
    public static void setup() {
        LootFunctions.init();
        ExtraLootCategories.init();
        GemInjectionRegistry.INSTANCE.registerToBus();
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
        Attributes.REGISTRY.register(bus);
        MobEffects.REGISTRY.register(bus);
        Enchantments.REGISTRY.register(bus);
        Particles.REGISTRY.register(bus);
        Sounds.REGISTRY.register(bus);
    }

    public static void init1(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private static RegistryObject<SoundEvent> registerSound(String path) {
        return Sounds.REGISTRY.register(path, () -> SoundEvent.createVariableRangeEvent(ApotheosisModernRagnarok.loc(path)));
    }

    private ModContent() {}
}
