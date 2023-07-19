package mod.chloeprime.apotheosismodernragnarok.common;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.ArmorSquashAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.BulletSaverAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.GunDamageAffix;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicLaser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.LootCategoryExtensions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shadows.apotheosis.adventure.affix.AffixManager;
import shadows.apotheosis.adventure.loot.LootCategory;
import shadows.placebo.json.DynamicRegistryObject;
import shadows.placebo.json.SerializerBuilder;

import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;

/**
 * Affix types:
 * apotheosis_modern_ragnarok:bullet_saver  shots have rate to return the bullet
 * apotheosis_modern_ragnarok:armor_squash  shots have rate to destroy target's armor
 */
public class ModContent {
    public static class LootCategories {
        /**
         * 所有枪械
         */
        public static final LootCategory GUN = LootCategoryExtensions.GUN;
    }

    public static class Affix {
        public static final DynamicRegistryObject<BulletSaverAffix> BULLET_SAVER = BulletSaverAffix.INSTANCE;
        public static final DynamicRegistryObject<ArmorSquashAffix> ARMOR_SQUASH = ArmorSquashAffix.INSTANCE;
    }

    public static class Entities {
        private static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, ApotheosisModernRagnarok.MOD_ID);
        public static final RegistryObject<EntityType<MagicLaser>> MAGIC_LASER = REGISTRY.register(
                "magic_laser",
                () -> EntityType.Builder.<MagicLaser>of(MagicLaser::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F)
                        .fireImmune()
                        .build("magic_laser")
        );
    }

    public static class Sounds {
        private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ApotheosisModernRagnarok.MOD_ID);
        public static final RegistryObject<SoundEvent> ARMOR_CRACK = register("affix.armor_break");

        private static RegistryObject<SoundEvent> register(String path) {
            return REGISTRY.register(path, () -> new SoundEvent(ApotheosisModernRagnarok.loc(path)));
        }
    }

    public static void setup() {
        AffixManager.INSTANCE.registerSerializer(loc("bullet_saver"), builder("Bullet Saver", BulletSaverAffix.class));
        AffixManager.INSTANCE.registerSerializer(loc("armor_squash"), builder("Armor Squash", ArmorSquashAffix.class));
        AffixManager.INSTANCE.registerSerializer(loc("damage_bonus"), builder("Gun Damage Bonus", GunDamageAffix.class));
    }

    private static SerializerBuilder<shadows.apotheosis.adventure.affix.Affix> builder(String name, Class<? extends shadows.apotheosis.adventure.affix.Affix> type) {
        return new SerializerBuilder<shadows.apotheosis.adventure.affix.Affix>(name).autoRegister(type);
    }

    public static void init0(IEventBus bus) {
        Entities.REGISTRY.register(bus);
        Sounds.REGISTRY.register(bus);
    }

    public static void init1(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private ModContent() {}
}
