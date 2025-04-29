package mod.chloeprime.apotheosismodernragnarok.common.affix.category;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.index.CommonGunIndex;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGunData;
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
                .filter(index -> !isDedicatedTaCZMeleeWeapon(index))
                .filter(index -> testGun(stack, gun, index))
                .isPresent();
    }

    static boolean isDedicatedTaCZMeleeWeapon(ItemStack stack) {
        return stack.getItem() instanceof IGun gun && TimelessAPI.getCommonGunIndex(gun.getGunId(stack))
                .filter(GunPredicate::isDedicatedTaCZMeleeWeapon)
                .isPresent();
    }

    static boolean isDedicatedTaCZMeleeWeapon(CommonGunIndex index) {
        int force = ((EnhancedGunData) index.getGunData()).amr$getApothData()
                .map(apoth -> apoth.force_melee_weapon)
                .orElse(0);
        if (force != 0) {
            return force > 0;
        }
        var range = index.getBulletData().getSpeed() * index.getBulletData().getLifeSecond() * 20F;
        return range <= 10;
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
