package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.entity.ProjectileEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ProjectileEntity.class, remap = false)
public interface ProjectileEntityAccessor {
    @Invoker
    void invokeOnHit(HitResult result, Vec3 startVec, Vec3 endVec);
}
