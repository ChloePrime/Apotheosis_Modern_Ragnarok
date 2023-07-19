package mod.chloeprime.apotheosismodernragnarok.common.internal;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.function.Function;

public class DynamicEnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
    /**
     * should be T::valueOf(String)
     */
    private final Function<String, T> nameToConstant;

    /**
     * should be T::name()
     */
    private final Function<T, String> constantToName;

    /**
     *
     * @param deserializer should be T::valueOf(String)
     * @param serializer should be T::name()
     */
    public DynamicEnumTypeAdapter(Function<String, T> deserializer, Function<T, String> serializer) {
        nameToConstant = deserializer;
        constantToName = serializer;
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return (T) nameToConstant.apply(in.nextString());
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        out.value(value == null ? null : constantToName.apply(value));
    }
}
