package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record GunDescriptor(
        ItemStack stack,
        IGun gunItem,
        CommonGunIndex data
) {
    public static Optional<GunDescriptor> of(ItemStack stack) {
        var gunItem = Optional.ofNullable(IGun.getIGunOrNull(stack));
        var data = gunItem.flatMap(igun -> TimelessAPI.getCommonGunIndex(igun.getGunId(stack)));
        return data.map(index -> new GunDescriptor(stack, gunItem.get(), index));
    }
}
