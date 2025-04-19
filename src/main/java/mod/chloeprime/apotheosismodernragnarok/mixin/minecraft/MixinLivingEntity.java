package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.chloeprime.apotheosismodernragnarok.common.internal.BulletSaverAffixUser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ProjectionMagicUser;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntity.class, priority = 1001)
public class MixinLivingEntity implements BulletSaverAffixUser, ProjectionMagicUser {
    private @Unique boolean amr$consumesBullet = true;
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
}
