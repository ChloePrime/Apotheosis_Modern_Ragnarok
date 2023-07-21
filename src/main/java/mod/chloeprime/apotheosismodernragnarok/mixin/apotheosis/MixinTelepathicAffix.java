package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import com.tac.guns.entity.DamageSourceProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.apotheosis.adventure.affix.effect.TelepathicAffix;

/**
 * 让枪械支持 Telepathic（掉落物传送到物品栏）词条
 */
@Mixin(value = TelepathicAffix.class, remap = false)
public class MixinTelepathicAffix {
    @Redirect(
            method = "drops",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;", ordinal = 2)
    )
    private Entity redirect_gunCompat(DamageSource source, LivingDropsEvent e) {
        if (e.getSource() instanceof DamageSourceProjectile) {
            return source.getEntity();
        }
        return source.getDirectEntity();
    }
}
