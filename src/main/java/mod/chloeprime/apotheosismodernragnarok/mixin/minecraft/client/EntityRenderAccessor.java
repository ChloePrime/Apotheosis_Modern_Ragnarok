package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface EntityRenderAccessor {
    @Invoker boolean invokeShouldShowName(Entity pEntity);
}
