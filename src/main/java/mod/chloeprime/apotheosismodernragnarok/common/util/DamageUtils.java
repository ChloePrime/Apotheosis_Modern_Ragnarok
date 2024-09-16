package mod.chloeprime.apotheosismodernragnarok.common.util;

import com.tacz.guns.api.item.IGun;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class DamageUtils {
    public static double getAttributeValueWithBase(LivingEntity holder, Attribute attribute, double newBase) {
        var instance = holder.getAttribute(attribute);
        if (instance == null) {
            return newBase;
        }

        var oldBase = instance.getBaseValue();
        try {
            instance.setBaseValue(newBase);
            return instance.getValue();
        } finally {
            instance.setBaseValue(oldBase);
        }
    }

    public static Optional<ItemStack> getWeapon(LivingEntity shooter, ResourceLocation gunIdToCheck) {
         return Optional.of(shooter.getMainHandItem())
                 .filter(stack -> Optional.ofNullable(IGun.getIGunOrNull(stack))
                         .map(gun -> gun.getGunId(stack))
                         .map(gunIdToCheck::equals)
                         .orElse(false));
    }

    private DamageUtils() {}
}
