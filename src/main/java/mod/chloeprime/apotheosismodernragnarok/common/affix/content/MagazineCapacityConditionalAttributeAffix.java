package mod.chloeprime.apotheosismodernragnarok.common.affix.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
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

public class MagazineCapacityConditionalAttributeAffix extends AttributeAffix {
    private final int miniumMagazineCapacity;

    public static final Codec<MagazineCapacityConditionalAttributeAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ForgeRegistries.ATTRIBUTES.getCodec().fieldOf("attribute").forGetter(a -> a.attribute),
                    PlaceboCodecs.enumCodec(AttributeModifier.Operation.class).fieldOf("operation").forGetter(a -> a.operation),
                    GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types),
                    Codec.INT.fieldOf("minium_magazine_capacity").forGetter(a -> a.miniumMagazineCapacity))
            .apply(inst, MagazineCapacityConditionalAttributeAffix::new));

    public MagazineCapacityConditionalAttributeAffix(
            Attribute attr,
            AttributeModifier.Operation op,
            Map<LootRarity, StepFunction> values,
            Set<LootCategory> types,
            int miniumMagazineCapacity
    ) {
        super(attr, op, values, types);
        this.miniumMagazineCapacity = miniumMagazineCapacity;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return super.canApplyTo(stack, cat, rarity) && Gunsmith.getGunInfo(stack)
                .map(gi -> gi.index().getGunData())
                .filter(gunData -> gunData.getAmmoAmount() >= this.miniumMagazineCapacity)
                // 背包供弹武器不支持弹匣容量词条
                .filter(gunData -> gunData.getReloadData().getType() != FeedType.INVENTORY)
                .isPresent();
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
