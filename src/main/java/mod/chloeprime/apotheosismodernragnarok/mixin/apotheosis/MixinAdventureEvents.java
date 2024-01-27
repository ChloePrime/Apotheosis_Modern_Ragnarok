package mod.chloeprime.apotheosismodernragnarok.mixin.apotheosis;

import com.tac.guns.Config;
import com.tac.guns.entity.DamageSourceProjectile;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import mod.chloeprime.apotheosismodernragnarok.common.util.DamageUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.apotheosis.adventure.AdventureEvents;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(value = AdventureEvents.class, remap = false)
public class MixinAdventureEvents {
    @Unique
    private float apotheosis_modern_ragnarok$originalAmount = -1;

    @Inject(method = {"pierce", "afterDamage"}, at = @At("HEAD"), cancellable = true)
    private void correctDamageHead0(LivingHurtEvent e, CallbackInfo ci) {
        apotheosis_modern_ragnarok$originalAmount = -1;
        apotheosis_modern_ragnarok$correctDamageHead(e, e.getSource(), e.getAmount(), e::setAmount, ci);
    }

    @Inject(method = {"pierce", "afterDamage"}, at = @At("RETURN"))
    private void correctDamageTail0(LivingHurtEvent e, CallbackInfo ci) {
        apotheosis_modern_ragnarok$correctDamageTail(e.getSource(), e::setAmount);
        apotheosis_modern_ragnarok$originalAmount = -1;
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void correctDamageHead1(LivingAttackEvent e, CallbackInfo ci) {
        apotheosis_modern_ragnarok$correctDamageHead(e, e.getSource(), e.getAmount(), null, ci);
    }

    @Unique
    private void apotheosis_modern_ragnarok$correctDamageHead(LivingEvent e, DamageSource source, float amount, @Nullable FloatConsumer setAmount, CallbackInfo ci) {
        if (!(source instanceof DamageSourceProjectile dsp)) {
            return;
        }
        if (!Config.COMMON.gameplay.bulletsIgnoreStandardArmor.get()) {
            return;
        }
        DamageUtils.ifIsKeptDamageOrElse(
                dsp, amount,
                fixedAmount -> {
                    if (setAmount != null) {
                        apotheosis_modern_ragnarok$originalAmount = amount;
                        setAmount.accept(fixedAmount);
                    }
                },
                ci::cancel
        );
    }

    @Unique
    private void apotheosis_modern_ragnarok$correctDamageTail(DamageSource source, @Nullable FloatConsumer setAmount) {
        if (setAmount == null) {
            return;
        }
        if (!(source instanceof DamageSourceProjectile)) {
            return;
        }
        if (!Config.COMMON.gameplay.bulletsIgnoreStandardArmor.get()) {
            return;
        }
        if (apotheosis_modern_ragnarok$originalAmount == -1) {
            return;
        }
        setAmount.accept(apotheosis_modern_ragnarok$originalAmount);
    }

    @Redirect(
            method = {"pierce", "afterDamage", "attack"},
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;")
    )
    private Entity makeAttributeTakeEffectOnGuns0(DamageSource source) {
        return source instanceof DamageSourceProjectile
                ? Optional.ofNullable(source.getEntity()).orElseGet(source::getDirectEntity)
                : source.getDirectEntity();
    }

    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/damagesource/DamageSource;isMagic()Z")
    )
    private boolean doNotNerfMyMagicGun(DamageSource source) {
        return !(source instanceof DamageSourceProjectile) && source.isMagic();
    }
}
