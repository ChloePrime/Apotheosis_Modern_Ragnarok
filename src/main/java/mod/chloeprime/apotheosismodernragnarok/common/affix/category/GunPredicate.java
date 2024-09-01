package mod.chloeprime.apotheosismodernragnarok.common.affix.category;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.function.Predicate;

@FunctionalInterface
public interface GunPredicate extends Predicate<ItemStack> {
    static GunPredicate matchIndex(Predicate<CommonGunIndex> predicate) {
        return of((stack, gun, index) -> predicate.test(index));
    }

    @Override
    default boolean test(ItemStack stack) {
        return stack.getItem() instanceof IGun gun && TimelessAPI.getCommonGunIndex(gun.getGunId(stack))
                .filter(index -> !isMeleeGun(index))
                .filter(index -> testGun(stack, gun, index))
                .isPresent();
    }

    static boolean isMeleeGun(ItemStack stack) {
        return stack.getItem() instanceof IGun gun && TimelessAPI.getCommonGunIndex(gun.getGunId(stack))
                .filter(GunPredicate::isMeleeGun)
                .isPresent();
    }

    static boolean isMeleeGun(CommonGunIndex index) {
        var range = index.getBulletData().getSpeed() * index.getBulletData().getLifeSecond() * 20F;
        return range <= 4;
    }

    boolean testGun(ItemStack stack, IGun gun, CommonGunIndex index);

    static GunPredicate of(GunPredicate lambda) {
        return lambda;
    }

    static GunPredicate any() {
        return of((g, s, i) -> true);
    }

    static GunPredicate supports(FireMode mode) {
        return of((g, s, index) -> index.getGunData().getFireModeSet().contains(mode));
    }

    static GunPredicate supports(FireMode... mode) {
        return of((g, s, index) -> Arrays.stream(mode).anyMatch(index.getGunData().getFireModeSet()::contains));
    }
}
