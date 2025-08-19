package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AttributeAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.util.StepFunction;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

public class ConditionalAttributeAffix extends AttributeAffix {
    private final int miniumMagazineCapacity;
    private final boolean disableOnInventoryBulletFeedGuns;

    public static final Codec<ConditionalAttributeAffix> CODEC = codec();

    public ConditionalAttributeAffix(
            Attribute attr,
            AttributeModifier.Operation op,
            Map<LootRarity, StepFunction> values,
            Set<LootCategory> types,
            int miniumMagazineCapacity,
            boolean disableOnInventoryBulletFeedGuns
    ) {
        super(attr, op, values, types);
        this.miniumMagazineCapacity = miniumMagazineCapacity;
        this.disableOnInventoryBulletFeedGuns = disableOnInventoryBulletFeedGuns;
    }

    public boolean canApplyTo(GunData gunData) {
        boolean ammoCap = gunData.getAmmoAmount() >= this.miniumMagazineCapacity;
        boolean feedTyp = !disableOnInventoryBulletFeedGuns || gunData.getReloadData().getType() != FeedType.INVENTORY;
        return ammoCap && feedTyp;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return super.canApplyTo(stack, cat, rarity) && Gunsmith.getGunInfo(stack)
                .map(gi -> gi.index().getGunData())
                .filter(this::canApplyTo)
                // 背包供弹武器不支持弹匣容量词条
                .filter(gunData -> gunData.getReloadData().getType() != FeedType.INVENTORY)
                .isPresent();
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

    private static Codec<ConditionalAttributeAffix> codec() {
        return RecordCodecBuilder.create(inst -> inst
                .group(
                        ForgeRegistries.ATTRIBUTES.getCodec().fieldOf("attribute").forGetter(a -> a.attribute),
                        PlaceboCodecs.enumCodec(AttributeModifier.Operation.class).fieldOf("operation").forGetter(a -> a.operation),
                        GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values),
                        LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types),
                        Codec.INT.optionalFieldOf("minium_magazine_capacity", 0).forGetter(a -> a.miniumMagazineCapacity),
                        Codec.BOOL.optionalFieldOf("disable_on_inventory_bullet_feed_guns", false).forGetter(a -> a.disableOnInventoryBulletFeedGuns))
                .apply(inst, ConditionalAttributeAffix::new));
    }

    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static final Codec<ConditionalAttributeAffix> CODEC_WITH_OLD_NAME = codec();
}
