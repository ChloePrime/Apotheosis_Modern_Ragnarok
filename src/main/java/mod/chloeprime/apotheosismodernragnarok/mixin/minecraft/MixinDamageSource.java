package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import mod.chloeprime.apotheosismodernragnarok.common.internal.DamageInfo;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public class MixinDamageSource implements DamageInfo {

    // 血子弹效果

    private @Unique float amr$defenseIgnoreRatio;
    private @Unique float amr$originalDamage;
    private @Unique boolean amr$attackFailed;

    @Override
    public float amr$getDefenseIgnoreRatio() {
        return amr$defenseIgnoreRatio;
    }

    @Override
    public void amr$setDefenseIgnoreRatio(float value) {
        amr$defenseIgnoreRatio = value;
    }

    @Override
    public float amr$getOriginalDamage() {
        return amr$originalDamage;
    }

    @Override
    public void amr$setOriginalDamage(float value) {
        amr$originalDamage = value;
    }

    @Override
    public boolean amr$isAttackFailed() {
        return amr$attackFailed;
    }

    @Override
    public void amr$setAttackFailed(boolean failed) {
        amr$attackFailed = failed;
    }
}
