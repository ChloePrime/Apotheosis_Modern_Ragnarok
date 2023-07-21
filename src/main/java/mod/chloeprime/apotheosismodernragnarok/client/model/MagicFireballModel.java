package mod.chloeprime.apotheosismodernragnarok.client.model;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicFireball;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

import java.util.Optional;

public class MagicFireballModel extends AnimatedTickingGeoModel<MagicFireball> {
    public static final ResourceLocation GEO = ApotheosisModernRagnarok.loc("geo/entity/magic_fireball.geo.json");
    public static final ResourceLocation TEX = ApotheosisModernRagnarok.loc("textures/entity/magic_fireball.png");
    public static final ResourceLocation ANIM = ApotheosisModernRagnarok.loc("animations/entity/magic_fireball.animation.json");

    @Override
    public ResourceLocation getModelLocation(MagicFireball object) {
        return GEO;
    }

    @Override
    public ResourceLocation getTextureLocation(MagicFireball object) {
        return TEX;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(MagicFireball animatable) {
        return ANIM;
    }

    @Override
    public void codeAnimations(MagicFireball entity, Integer uniqueID, AnimationEvent<?> e) {
        super.codeAnimations(entity, uniqueID, e);
        Optional.ofNullable(this.getAnimationProcessor().getBone("Root")).ifPresent(root -> {
            var age = e.animationTick;
            var spawnScale = 1 - Math.pow(4, -0.25 * age);
            var despawnScale = Math.max(0, 1 - Math.pow(1.5, -1 * (entity.life - age)));
            var scale = (float) (spawnScale * despawnScale);
            root.setScaleX(scale);
            root.setScaleY(scale);
            root.setScaleZ(scale);

            float rotationPhase = (float) (age - Math.floor(seekTime / 10) * 10) / 10;
            root.setRotationY(rotationPhase * Mth.TWO_PI);
        });
    }
}
