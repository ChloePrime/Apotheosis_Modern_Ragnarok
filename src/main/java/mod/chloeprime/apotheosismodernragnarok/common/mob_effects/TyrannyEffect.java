package mod.chloeprime.apotheosismodernragnarok.common.mob_effects;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.awt.*;

public class TyrannyEffect extends MobEffectBaseUtility {
    public static final ResourceLocation RPM_MODIFIER_UUID = ApotheosisModernRagnarok.loc("tyranny_rpm_buff");
    public static final ResourceLocation SPEED_MODIFIER_UUID = ApotheosisModernRagnarok.loc("tyranny_speed_debuff");

    public TyrannyEffect(MobEffectCategory category, Color color) {
        super(category, color.getRGB());
    }

    public static TyrannyEffect create() {
        return (TyrannyEffect) new TyrannyEffect(MobEffectCategory.BENEFICIAL, new Color(184, 51, 54, 255))
                .addAttributeModifier(GunAttributes.RPM, RPM_MODIFIER_UUID, 8, AttributeModifier.Operation.ADD_VALUE)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_UUID, -0.0075, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
