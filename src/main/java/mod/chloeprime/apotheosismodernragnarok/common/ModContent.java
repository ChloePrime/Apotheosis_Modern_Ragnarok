package mod.chloeprime.apotheosismodernragnarok.common;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.*;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicFireball;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicLaser;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

import static mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok.loc;

/**
 * Affix types:
 * apotheosis_modern_ragnarok:bullet_saver  shots have rate to return the bullet
 * apotheosis_modern_ragnarok:armor_squash  shots have rate to destroy target's armor
 */
public class ModContent {
    public static class Items {
        private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ApotheosisModernRagnarok.MOD_ID);
        public static final RegistryObject<Item> ANCIENT_MATERIAL = REGISTRY.register(
                "izanagi_object",
                () -> new SalvageItem(LootRarity.ANCIENT, new Item.Properties().tab(Apotheosis.APOTH_GROUP))
        );

        private Items() {}
    }
    public static class LootCategories extends ExtraLootCategories {
        private LootCategories() {}
    }

    public static class Affix {
        public static final DynamicHolder<ArmorSquashAffix> ARMOR_SQUASH = holder("armor_squash");
        public static final DynamicHolder<BulletSaverAffix> BULLET_SAVER = holder("bullet_saver");

        private static <T extends dev.shadowsoffire.apotheosis.adventure.affix.Affix> DynamicHolder<T> holder(String path) {
            return AffixRegistry.INSTANCE.holder(ApotheosisModernRagnarok.loc(path));
        }

        private Affix() {}
    }

    public static class Entities {
        private static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, ApotheosisModernRagnarok.MOD_ID);
        public static final RegistryObject<EntityType<MagicLaser>> MAGIC_LASER = registerEntity(
                "magic_laser", MagicLaser::new, MobCategory.MISC,
                builder -> builder.sized(0.25F, 0.25F).fireImmune()
                        .setTrackingRange(128).setUpdateInterval(20).setShouldReceiveVelocityUpdates(false)
        );

        public static final RegistryObject<EntityType<MagicFireball>> MAGIC_FIREBALL = registerEntity(
                "magic_fireball", MagicFireball::new, MobCategory.MISC,
                builder -> builder.sized(0.8F, 0.8F).fireImmune()
                        .setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true)
        );

        private Entities() {}
    }

    public static class Sounds {
        private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ApotheosisModernRagnarok.MOD_ID);
        public static final RegistryObject<SoundEvent> ARMOR_CRACK = registerSound("affix.armor_break");
        public static final RegistryObject<SoundEvent> MAGIC_SHOT = registerSound("affix.magical.shot");
        public static final RegistryObject<SoundEvent> MAGIC_DANMAKU = registerSound("affix.magical.danmaku");
        public static final RegistryObject<SoundEvent> MAGIC_FIREBALL = registerSound("affix.magical.fireball");
        public static final RegistryObject<SoundEvent> HEAD_EXPLOSION = registerSound("affix.head_explosion");

        private Sounds() {}
    }

    public static void setup() {
        AdventureModule.RARITY_MATERIALS.putIfAbsent(LootRarity.ANCIENT, Items.ANCIENT_MATERIAL.get().delegate);
    }

    private static SerializerBuilder<dev.shadowsoffire.apotheosis.adventure.affix.Affix> builder(String name, Class<? extends shadows.apotheosis.adventure.affix.Affix> type) {
        return new SerializerBuilder<dev.shadowsoffire.apotheosis.adventure.affix.Affix>(name).autoRegister(type);
    }

    public static void init0(IEventBus bus) {
        Items.REGISTRY.register(bus);
        Entities.REGISTRY.register(bus);
        Sounds.REGISTRY.register(bus);
        AffixRegistry.INSTANCE.registerCodec(loc("bullet_saver"), builder("Bullet Saver", BulletSaverAffix.class));
        AffixRegistry.INSTANCE.registerCodec(loc("armor_squash"), ArmorSquashAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(loc("damage_bonus"), builder("Gun Damage Bonus", GunDamageAffix.class));
        AffixRegistry.INSTANCE.registerCodec(loc("ammo_capacity"), builder("Ammo Capacity", AmmoCapacityAffix.class));
        AffixRegistry.INSTANCE.registerCodec(loc("explode_on_headshot"), builder("Explode On Headshot", ExplosionOnHeadshotAffix.class));
        AffixRegistry.INSTANCE.registerCodec(loc("ads_charge"), builder("Aim Down Shoot Charging", AdsChargeAffix.class));
    }

    public static void init1(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private static RegistryObject<SoundEvent> registerSound(String path) {
        return Sounds.REGISTRY.register(path, () -> new SoundEvent(ApotheosisModernRagnarok.loc(path)));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(
            String path,
            EntityType.EntityFactory<T> constructor,
            MobCategory category,
            Consumer<EntityType.Builder<T>> options) {
        return Entities.REGISTRY.register(path, () -> {
            var builder = EntityType.Builder.of(constructor, category);
            options.accept(builder);
            return builder.build(path);
        });
    }

    private ModContent() {}
}
