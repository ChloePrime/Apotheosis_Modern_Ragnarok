package mod.chloeprime.apotheosismodernragnarok.common.affix;

import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractAffix extends Affix {
    protected final Set<LootCategory> categories;

    public AbstractAffix(AffixType type, Set<LootCategory> categories) {
        super(type);
        this.categories = categories;
    }

    public Set<LootCategory> getApplicableCategories() {
        return categories;
    }

    public static String fmt(double value) {
        return fmt(((float) value));
    }

    public String desc() {
        return "affix." + getId() + ".desc";
    }

    public static boolean isStillHoldingTheSameGun(ItemStack gunStack, @Nonnull ResourceLocation gunId) {
        return Optional.ofNullable(IGun.getIGunOrNull(gunStack))
                .map(g -> g.getGunId(gunStack))
                .filter(gunId::equals)
                .isPresent();
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory category, LootRarity rarity) {
        if (category == LootCategory.NONE) {
            return false;
        }
        var validTypes = getApplicableCategories();
        return validTypes.isEmpty() || validTypes.contains(category);
    }
}
