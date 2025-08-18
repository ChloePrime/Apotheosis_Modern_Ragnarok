package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public abstract class AffixBaseUtility extends Affix {
    public static final Style BRIGHT_RED = Style.EMPTY.withColor(TextColor.parseColor("#FF8080"));

    public AffixBaseUtility(AffixType type) {
        super(type);
    }

    public static String fmt(double value) {
        return fmt((float) value);
    }

    public static Component fmtAugmenting(double value, double min, double max) {
        return fmtAugmenting((float) value, (float) min, (float) max);
    }

    public static Component fmtAugmenting(float value, float min, float max) {
        var baseRateText = fmt(value);
        if (min == max) {
            return Component.literal(baseRateText);
        } else {
            return Component.literal(baseRateText).append(
                    Component.literal(" [%s - %s]".formatted(
                            fmt(min),
                            fmt(max)
                    )).withStyle(ChatFormatting.DARK_GRAY)
            );
        }
    }

    public static String fmtPercent(double value) {
        return fmtPercent((float) value);
    }

    public static String fmtPercent(float value) {
        return fmt(100 * value) + '%';
    }

    public static Component fmtPercents(double value, double min, double max) {
        return fmtPercents((float) value, (float) min, (float) max);
    }

    public static Component fmtPercents(float value, float min, float max) {
        var baseRateText = fmt(100 * value) + "%";
        if (min == max) {
            return Component.literal(baseRateText);
        } else {
            return Component.literal(baseRateText).append(
                    Component.literal(" [%s%% - %s%%]".formatted(
                            fmt(100 * min),
                            fmt(100 * max)
                    )).withStyle(ChatFormatting.DARK_GRAY)
            );
        }
    }
}
