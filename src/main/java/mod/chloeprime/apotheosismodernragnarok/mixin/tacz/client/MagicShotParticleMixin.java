package mod.chloeprime.apotheosismodernragnarok.mixin.tacz.client;

import com.tacz.guns.client.particle.AmmoParticleSpawner;
import com.tacz.guns.client.resource.pojo.display.ammo.AmmoParticle;
import com.tacz.guns.entity.EntityKineticBullet;
import mod.chloeprime.apotheosismodernragnarok.client.MagicalShotAffixVisuals;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.MagicalShotAffix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AmmoParticleSpawner.class, remap = false)
public abstract class MagicShotParticleMixin {
    @Inject(method = "addParticle", at = @At("HEAD"))
    private static void spawnMagicShotParticle(EntityKineticBullet bullet, CallbackInfo ci) {
        if (MagicalShotAffix.clientIsMagicBullet(bullet)) {
            spawnParticle(bullet, MagicalShotAffixVisuals.MAGIC_PARTICLE);
        }
    }

    @Shadow
    private static void spawnParticle(EntityKineticBullet bullet, AmmoParticle particle) {
        throw new AbstractMethodError();
    }
}
