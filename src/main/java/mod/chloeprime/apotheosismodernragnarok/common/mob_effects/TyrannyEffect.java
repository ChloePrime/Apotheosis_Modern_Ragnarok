package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.awt.*;
import java.util.UUID;

public class TyrannyEffect extends MobEffect {
    public static final UUID RPM_MODIFIER_UUID = UUID.fromString("35b0e7ed-2976-4082-bc0c-d4102a2506c1");
    public static final UUID SPEED_MODIFIER_UUID = UUID.fromString("bc3b84fb-de99-415b-9ef9-309c04339b1a");

    public TyrannyEffect(MobEffectCategory category, Color color) {
        super(category, color.getRGB());
    }

    public static TyrannyEffect create() {
        return (TyrannyEffect) new TyrannyEffect(MobEffectCategory.BENEFICIAL, new Color(184, 51, 54, 255))
                .addAttributeModifier(GunAttributes.RPM.get(), RPM_MODIFIER_UUID.toString(), 8, AttributeModifier.Operation.ADDITION)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_UUID.toString(), -0.0075, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
