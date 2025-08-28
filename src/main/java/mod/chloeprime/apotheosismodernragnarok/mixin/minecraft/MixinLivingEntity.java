package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.enchantment.PerfectBlockEnchantment;
import mod.chloeprime.apotheosismodernragnarok.common.internal.*;
import mod.chloeprime.apotheosismodernragnarok.common.util.PostureSystem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 1001)
public
abstract class MixinLivingEntity extends Entity implements
        BulletSaverAffixUser,
        ProjectionMagicUser,
        PerfectBlockEnchantmentUser,
        BloodBulletUser
{
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

    // 血子弹效果

    private @Unique float amr$defenseIgnoreRatio;

    @Override
    public float amr$getDefenseIgnoreRatio() {
        return amr$defenseIgnoreRatio;
    }

    @Override
    public void amr$setDefenseIgnoreRatio(float value) {
        amr$defenseIgnoreRatio = value;
    }

    // 完美招架附魔

    private @Unique long amr$cannotPerfectBlockEndTime;

    @Override
    public long amr$getPerfectBlockEndTime() {
        return getData(ModContent.SinceMC1211.DataAttachments.PERFECT_BLOCK_END_TIME);
    }

    @Override
    public void amr$setPerfectBlockEndTime(long value) {
        setData(ModContent.SinceMC1211.DataAttachments.PERFECT_BLOCK_END_TIME, value);
    }

    @Override
    public boolean amr$canUsePerfectBlock() {
        var now = level().getGameTime();
        return now >= amr$getCannotPerfectBlockEndTime();
    }

    @Override
    public long amr$getCannotPerfectBlockEndTime() {
        return amr$cannotPerfectBlockEndTime;
    }

    @Override
    public void amr$setCannotPerfectBlockEndTime(long value) {
        amr$cannotPerfectBlockEndTime = value;
    }

    @WrapOperation(
            method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;serverAiStep()V"))
    private void disableAiWhenUnbalancing(LivingEntity receiver, Operation<Void> original) {
        if ((Object) this instanceof Player) {
            original.call(receiver);
            return;
        }
        if (PostureSystem.isPostureBroken(receiver)) {
            noJumpDelay = Math.max(noJumpDelay, 5);
            xxa = zza = 0;
            if ((Object) this instanceof Mob mob) {
                mob.getNavigation().stop();
            }
        } else {
            original.call(receiver);
        }
    }

    @Inject(method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/CommonHooks;onEntityIncomingDamage(Lnet/minecraft/world/entity/LivingEntity;Lnet/neoforged/neoforge/common/damagesource/DamageContainer;)Z",
                    remap = false,
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void onDetectInvulnerability(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        var user = (LivingEntity) (Object) this;
        // 失衡被处决
        if (PerfectBlockEnchantment.tryExecute(user, source, amount)) {
            return;
        }
        // 格挡判定
        if (!PerfectBlockEnchantment.canTrigger(user, source)) {
            return;
        }
        var now = level().getGameTime();
        if (now > amr$getPerfectBlockEndTime()) {
            return;
        }
        PerfectBlockEnchantment.onPerfectBlockTriggered(user, source);
        cir.setReturnValue(false);
    }

    @Shadow private int noJumpDelay;
    @Shadow public float xxa;
    @Shadow public float zza;
}
