package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.pojo.data.attachment.EffectData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(value = ModernKineticGunItem.class, remap = false)
public class MixinModernKineticGunItem {
    @ModifyVariable(method = "doMelee", at = @At("HEAD"), ordinal = 3, argsOnly = true)
    private float knockbackEnchantments(float oldKnockback, LivingEntity user) {
        return oldKnockback + 0.5F * EnchantmentHelper.getKnockbackBonus(user);
    }

    @ModifyVariable(method = "doPerLivingHurt", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static float sharpnessEnchantments(float oldDamage, LivingEntity user, LivingEntity target, float knockback, float ignored, List<EffectData> effects) {
        return oldDamage + EnchantmentHelper.getDamageBonus(user.getMainHandItem(), target.getMobType());
    }
}
