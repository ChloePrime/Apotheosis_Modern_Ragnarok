package mod.chloeprime.apotheosismodernragnarok.common.enchantment.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import javax.annotation.Nonnull;

public record FusedMultiplyAddFormula(
        LevelBasedValue a,
        LevelBasedValue b,
        LevelBasedValue c
) implements LevelBasedValue {
    public static FusedMultiplyAddFormula add(LevelBasedValue v1, LevelBasedValue v2) {
        return new FusedMultiplyAddFormula(LevelBasedValue.constant(1), v1, v2);
    }

    public static FusedMultiplyAddFormula mul(LevelBasedValue v1, LevelBasedValue v2) {
        return new FusedMultiplyAddFormula(v1, v2, LevelBasedValue.constant(0));
    }

    public static final MapCodec<FusedMultiplyAddFormula> CODEC = RecordCodecBuilder.mapCodec(p_344815_ -> p_344815_.group(
            LevelBasedValue.CODEC.fieldOf("a").forGetter(FusedMultiplyAddFormula::a),
            LevelBasedValue.CODEC.fieldOf("b").forGetter(FusedMultiplyAddFormula::b),
            LevelBasedValue.CODEC.fieldOf("c").forGetter(FusedMultiplyAddFormula::c)
    ).apply(p_344815_, FusedMultiplyAddFormula::new));

    @Override
    public float calculate(int level) {
        var result = Math.fma(a.calculate(level), b.calculate(level), c.calculate(level));
        return Float.isFinite(result) ? result : 0;
    }

    @Override
    public @Nonnull MapCodec<? extends LevelBasedValue> codec() {
        return CODEC;
    }
}
