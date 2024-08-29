package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.item.ModernKineticGunItem;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.BulletSaverAffix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(value = ModernKineticGunItem.class, remap = false)
public class MixinModernKineticGunItem {
    /**
     * 概率不消耗子弹
     * @see BulletSaverAffix
     */
    @Mixin(value = ModernKineticGunItem.class, remap = false)
    public static class BulletSaverAffixMixin {
        @ModifyExpressionValue(method = "shoot", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/IGunOperator;consumesAmmoOrNot()Z"))
        private boolean onReduceAmmo(boolean original, ItemStack gunItem, Supplier<Float> pitch, Supplier<Float> yaw, boolean tracer, LivingEntity shooter) {
            return original && !BulletSaverAffix.check(shooter.getRandom(), gunItem);
        }
    }
}
