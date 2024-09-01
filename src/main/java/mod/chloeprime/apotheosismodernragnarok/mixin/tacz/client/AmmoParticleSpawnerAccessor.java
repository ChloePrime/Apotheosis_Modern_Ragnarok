package mod.chloeprime.apotheosismodernragnarok.mixin.tacz.client;

import com.tacz.guns.client.particle.AmmoParticleSpawner;
import com.tacz.guns.client.resource.pojo.display.ammo.AmmoParticle;
import com.tacz.guns.entity.EntityKineticBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AmmoParticleSpawner.class, remap = false)
public interface AmmoParticleSpawnerAccessor {
    @Invoker static void invokeSpawnParticle(EntityKineticBullet bullet, AmmoParticle particle) {
        throw new AssertionError();
    }
}
