package mod.chloeprime.apotheosismodernragnarok.common.enchantment.component;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.openjdk.nashorn.api.scripting.ClassFilter;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public record NashornJavascriptValue(
        String code,
        Int2FloatFunction script
) implements LevelBasedValue {
    public static final MapCodec<NashornJavascriptValue> CODEC = RecordCodecBuilder.mapCodec(p_344815_ -> p_344815_.group(
            Codec.STRING.fieldOf("code").forGetter(NashornJavascriptValue::code)
    ).apply(p_344815_, NashornJavascriptValue::new));

    public NashornJavascriptValue(String code) {
        this(code, compile(code));
    }

    public static NashornJavascriptValue forDatagen(String code) {
        return new NashornJavascriptValue(code, level -> 0);
    }

    @Override
    public float calculate(int level) {
        return script.get(level);
    }

    @Override
    public @Nonnull MapCodec<? extends LevelBasedValue> codec() {
        return CODEC;
    }

    private static final NashornScriptEngineFactory FACTORY = new NashornScriptEngineFactory();
    private static final ThreadLocal<Bindings> BINDINGS = ThreadLocal.withInitial(SimpleBindings::new);
    private static final ClassFilter DISABLE_JAVA_ACCESS = Predicates.alwaysFalse()::apply;

    private static Int2FloatFunction compile(String code) {
        try {
            var engine = ((Compilable) FACTORY.getScriptEngine(DISABLE_JAVA_ACCESS));
            var script = engine.compile(code);
            return level -> {
                var bindings = BINDINGS.get();
                bindings.put("x", level);
                bindings.put("level", level);
                try {
                    return ((Number) script.eval(bindings)).floatValue();
                } catch (ScriptException ex) {
                    ApotheosisModernRagnarok.LOGGER.warn("Failed to evaluate Javascript level based value: ", ex);
                    return 0;
                }
            };
        } catch (ScriptException ex) {
            ApotheosisModernRagnarok.LOGGER.warn("Failed to compile Javascript level based value: ", ex);
            return level -> 0;
        }
    }
}
