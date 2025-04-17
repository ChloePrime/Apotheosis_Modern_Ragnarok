package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import mod.chloeprime.apotheosismodernragnarok.common.internal.ProjectionMagicUser;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class MixinLivingEntity implements ProjectionMagicUser {
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
}
