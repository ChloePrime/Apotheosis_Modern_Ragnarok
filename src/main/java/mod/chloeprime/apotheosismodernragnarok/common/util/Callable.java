package mod.chloeprime.apotheosismodernragnarok.common.util;

@FunctionalInterface
public interface Callable<V> extends java.util.concurrent.Callable<V> {
    V call();
}