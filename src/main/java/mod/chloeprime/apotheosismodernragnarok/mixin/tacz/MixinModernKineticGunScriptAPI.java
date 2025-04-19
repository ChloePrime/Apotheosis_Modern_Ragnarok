package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.BulletSaverAffix;
import mod.chloeprime.apotheosismodernragnarok.common.internal.BulletSaverAffixUser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class MixinModernKineticGunScriptAPI {
    /**
     * 概率不消耗子弹
     * @see BulletSaverAffix
     */
    @Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
    public static class BulletSaverAffixMixin {
        @Shadow private LivingEntity shooter;
        @Shadow private ItemStack itemStack;

        @ModifyReturnValue(method = "isShootingNeedConsumeAmmo", at = @At("RETURN"))
        private boolean onReduceAmmo(boolean original) {
            // 创造模式或者满足其他不消耗弹药的条件
            // 那么不再进行判定
            if (!original) {
                return false;
            }
            var gun = this.itemStack;
            var shooter = this.shooter;
            if (shooter == null || gun == null || gun.isEmpty() || shooter.level().isClientSide) {
                return true;
            }
            var user = (BulletSaverAffixUser) shooter;
            return user.amr$willConsumesBullet();
        }
    }
}
