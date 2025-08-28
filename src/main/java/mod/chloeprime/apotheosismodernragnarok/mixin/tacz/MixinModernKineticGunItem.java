package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ModernKineticGunItem.class, remap = false)
public class MixinModernKineticGunItem {
    @ModifyVariable(method = "doMelee", at = @At("HEAD"), ordinal = 3, argsOnly = true)
    private float knockbackEnchantments(float oldKnockback, LivingEntity user) {
        if (user.level() instanceof ServerLevel level) {
            var source = user instanceof Player player
                    ? user.damageSources().playerAttack(player)
                    : user.damageSources().mobAttack(user);
            return EnchantmentHelper.modifyKnockback(level,  user.getMainHandItem(), user, source, oldKnockback);
        } else {
            return oldKnockback;
        }
    }

    @ModifyVariable(method = "doPerLivingHurt", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static float sharpnessEnchantments(float oldDamage, LivingEntity user) {
        if (user.level() instanceof ServerLevel level) {
            var source = user instanceof Player player
                    ? user.damageSources().playerAttack(player)
                    : user.damageSources().mobAttack(user);
            return EnchantmentHelper.modifyKnockback(level,  user.getMainHandItem(), user, source, oldDamage);
        } else {
            return oldDamage;
        }
    }
}
