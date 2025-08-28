package mod.chloeprime.apotheosismodernragnarok.data;

import com.tacz.guns.init.ModItems;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.component.NashornJavascriptValue;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.MultiplyValue;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static mod.chloeprime.apotheosismodernragnarok.common.ModContent.Enchantments.*;
import static mod.chloeprime.apotheosismodernragnarok.common.ModContent.SinceMC1211.EnchantmentEffectComponents.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;
import static net.minecraft.world.item.enchantment.Enchantment.*;

public class AMREnchantProvider {
    private static BootstrapContext<Enchantment> context;
    private static final HolderSet<Item> ALL_GUNS = HolderSet.direct(ModItems.MODERN_KINETIC_GUN);

    private static EnchantmentAttributeEffect modifier(
            String path,
            Holder<Attribute> attribute,
            LevelBasedValue value,
            AttributeModifier.Operation operation
    ) {
        var id =  ApotheosisModernRagnarok.loc("enchantment." + path);
        return new EnchantmentAttributeEffect(id, attribute, value, operation);
    }

    private static LevelBasedValue javascript(String code) {
        return NashornJavascriptValue.forDatagen(code);
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        AMREnchantProvider.context = context;
        var lookup = context.lookup(Registries.ENCHANTMENT);
        register(STABILITY, Enchantment
                .enchantment(definition(ALL_GUNS, 10, 6, dynamicCost(5, 10), dynamicCost(55, 25), 1, EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, modifier("stability", GunAttributes.H_RECOIL, javascript("30 / (30 + level)"), ADD_MULTIPLIED_TOTAL))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, modifier("stability", GunAttributes.V_RECOIL, javascript("30 / (30 + level)"), ADD_MULTIPLIED_TOTAL))
        );
        register(EMERGENCY_PROTECTOR, Enchantment
                .enchantment(definition(ALL_GUNS, 10, 6, dynamicCost(5, 10), dynamicCost(55, 25), 1, EquipmentSlotGroup.OFFHAND))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, modifier("emergency_protector", Attributes.ARMOR, LevelBasedValue.perLevel(1, 1), ADD_VALUE))
        );
        register(LAST_STAND, Enchantment
                .enchantment(definition(ALL_GUNS, 3, 2, dynamicCost(5, 20), dynamicCost(55, 20), 2, EquipmentSlotGroup.MAINHAND))
                .withEffect(ATTRIBUTES_WHEN_AMMO_EMPTY.get(), modifier("last_stand", Attributes.ATTACK_DAMAGE, LevelBasedValue.perLevel(0.1F), ADD_MULTIPLIED_TOTAL))
        );
        register(RIPTIDE_WARHEAD, Enchantment
                .enchantment(definition(ALL_GUNS, 5, 4, dynamicCost(10, 15), dynamicCost(50, 15), 2, EquipmentSlotGroup.MAINHAND))
                .withSpecialEffect(BULLET_UNDERWATER_FRICTION.get(), new MultiplyValue(javascript("1.0 / (1 + level)")))
        );
        register(SURVIVAL_INSTINCT, Enchantment
                .enchantment(definition(ALL_GUNS, 2, 3, dynamicCost(15, 9), dynamicCost(65, 9), 3, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(lookup.getOrThrow(BULLET_REGENERATION_EXCLUSIVE))
                .withSpecialEffect(BULLET_DROP_RATE.get(), new AddValue(javascript("1 - 16.0 / (level + 16)")))
        );
        register(PROJECTION_MAGIC, Enchantment
                .enchantment(definition(HolderSet.empty(), 1, 6, dynamicCost(57, 53), constantCost(50000), 10, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(lookup.getOrThrow(BULLET_REGENERATION_EXCLUSIVE))
                .withCustomName(c -> c.withStyle(ChatFormatting.DARK_GREEN))
                .withSpecialEffect(PROJECTION_MAGIC_DELAY.get(), new SetValue(javascript("1 + (2.0 / 3) / (level - 1.0 / 3)")))
        );
        register(PERFECT_BLOCK, Enchantment
                .enchantment(definition(HolderSet.empty(), 2, 3, dynamicCost(15, 25), dynamicCost(55, 25), 4, EquipmentSlotGroup.MAINHAND))
                .withCustomName(c -> c.withStyle(ChatFormatting.DARK_GREEN))
                .withSpecialEffect(PERFECT_BLOCK_TIME_WINDOW.get(), new AddValue(LevelBasedValue.perLevel(2, 1)))
        );
    }

    private static void register(ResourceKey<Enchantment> key, Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private static ResourceKey<Enchantment> loc(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, ApotheosisModernRagnarok.loc(path));
    }

    public static class Tags extends EnchantmentTagsProvider {
        public Tags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, ApotheosisModernRagnarok.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(@Nonnull HolderLookup.Provider provider) {
            tag(CAT_HAS_MAGAZINE).add(PROJECTION_MAGIC);
            tag(CAT_MELEE_CAPABLE).add(PERFECT_BLOCK);
            tag(BULLET_REGENERATION_EXCLUSIVE).add(SURVIVAL_INSTINCT, PROJECTION_MAGIC);
            tag(AVAILABLE_FOR_GUNS)
                    .add(Enchantments.LOOTING)
                    .addOptional(ResourceLocation.parse("apothic_enchanting:knowledge_of_the_ages"))
                    .addOptional(ResourceLocation.parse("apothic_enchanting:scavenger"))
                    .addOptional(ResourceLocation.parse("apothic_spawners:capturing"));
        }
    }
}
