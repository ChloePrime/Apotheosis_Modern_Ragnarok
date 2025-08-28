package mod.chloeprime.apotheosismodernragnarok.mixin.tacz;

import com.tacz.guns.entity.EntityKineticBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityKineticBullet.class, remap = false)
public interface EntityKineticBulletAccessor {
    @Accessor int getLife();
    @Accessor float getKnockback();
    @Accessor void setKnockback(float knockback);
}
