package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.tacz.guns.item.ModernKineticGunScriptAPI;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.BulletSaverAffix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

        @Inject(method = "isShootingNeedConsumeAmmo", at = @At("HEAD"), cancellable = true)
        private void onReduceAmmo(CallbackInfoReturnable<Boolean> cir) {
            var gun = this.itemStack;
            var shooter = this.shooter;
            if (shooter == null || gun == null || gun.isEmpty() || shooter.level().isClientSide) {
                return;
            }
            if (BulletSaverAffix.check(shooter.getRandom(), gun)) {
                cir.setReturnValue(false);
            }
        }
    }
}
