package mod.chloeprime.apotheosismodernragnarok.common.internal;

import net.minecraft.world.entity.Entity;

public interface PreRenderListener<E extends Entity> {
    boolean preRender(E entity, float partial);
}
