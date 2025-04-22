package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.PerfectBlockEnchantment;
import mod.chloeprime.apotheosismodernragnarok.common.internal.BulletSaverAffixUser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.PerfectBlockEnchantmentUser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ProjectionMagicUser;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 1001)
public abstract class MixinLivingEntity extends Entity implements BulletSaverAffixUser, ProjectionMagicUser, PerfectBlockEnchantmentUser {
    @ApiStatus.Internal
    public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // 投影魔术附魔

    private @Unique boolean amr$projectionMagicActivated;
    private @Unique long amr$projectionMagicStartEta;

    @Override
    public boolean amr$activateProjectionMagic() {
        var activatedBefore = amr$projectionMagicActivated;
        amr$projectionMagicActivated = true;
        return !activatedBefore;
    }

    @Override
    public void amr$deactivateProjectionMagic() {
        amr$projectionMagicActivated = false;
    }

    @Override
    public long amr$getProjectionMagicStartEta() {
        return amr$projectionMagicStartEta;
    }

    @Override
    public void amr$setProjectionMagicStartEta(long value) {
        amr$projectionMagicStartEta = value;
    }

    // 概率不消耗弹药效果

    private @Unique boolean amr$consumesBullet = true;

    @Override
    public boolean amr$willConsumesBullet() {
        return amr$consumesBullet;
    }

    @Override
    public void amr$setConsumesBullet(boolean value) {
        amr$consumesBullet = value;
    }

    @Dynamic
    @ModifyReturnValue(method = "consumesAmmoOrNot", at = @At("RETURN"), remap = false)
    public boolean overrideConsumesAmmoOrNot(boolean value) {
        return value && amr$willConsumesBullet();
    }

    // 完美招架附魔

    private @Unique long amr$perfectBlockEndTime;

    @Override
    public long amr$getPerfectBlockEndTime() {
        return amr$perfectBlockEndTime;
    }

    @Override
    public void amr$setPerfectBlockEndTime(long value) {
        amr$perfectBlockEndTime = value;
    }

    @Inject(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;onLivingAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    remap = false,
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void onDetectInvulnerability(DamageSource source, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        var user = (LivingEntity) (Object) this;
        if (!PerfectBlockEnchantment.canTrigger(user, source)) {
            return;
        }
        var now = level().getGameTime();
        if (now > amr$getPerfectBlockEndTime()) {
            return;
        }
        amr$setPerfectBlockEndTime(0);
        PerfectBlockEnchantment.onPerfectBlockTriggered(user, source);
        cir.setReturnValue(false);
    }
}
