package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import mod.chloeprime.apotheosismodernragnarok.common.internal.BulletSaverAffixUser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ProjectionMagicUser;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
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
}
