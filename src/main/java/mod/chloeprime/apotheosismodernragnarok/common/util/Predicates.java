package mod.chloeprime.apotheosismodernragnarok.common.util;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class Predicates {
    public static <T> Predicate<T> acceptAll() {
        return (Predicate<T>) ALWAYS_TRUE;
    }

    public static <T> Predicate<T> refuseAll() {
        return (Predicate<T>) ALWAYS_FALSE;
    }

    private static final Predicate<?> ALWAYS_TRUE = o -> true;
    private static final Predicate<?> ALWAYS_FALSE = o -> false;
}
