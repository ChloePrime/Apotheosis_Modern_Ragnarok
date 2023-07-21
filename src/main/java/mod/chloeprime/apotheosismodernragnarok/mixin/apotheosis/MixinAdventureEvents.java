package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import com.tac.guns.entity.DamageSourceProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.apotheosis.adventure.AdventureEvents;

import java.util.Optional;

@Mixin(value = AdventureEvents.class, remap = false)
public class MixinAdventureEvents {
    @Redirect(
            method = "pierce",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;")
    )
    private Entity makeAttributeTakeEffectOnGuns0(DamageSource source) {
        return apotheosis_modern_ragnarok$redirect0(source);
    }

    @Redirect(
            method = "afterDamage",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;")
    )
    private Entity makeAttributeTakeEffectOnGuns1(DamageSource source) {
        return apotheosis_modern_ragnarok$redirect0(source);
    }

    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;")
    )
    private Entity makeAttributeTakeEffectOnGuns12(DamageSource source) {
        return apotheosis_modern_ragnarok$redirect0(source);
    }

    @Unique
    private static Entity apotheosis_modern_ragnarok$redirect0(DamageSource source) {
        return source instanceof DamageSourceProjectile
                ? Optional.ofNullable(source.getEntity()).orElseGet(source::getDirectEntity)
                : source.getDirectEntity();
    }
}
