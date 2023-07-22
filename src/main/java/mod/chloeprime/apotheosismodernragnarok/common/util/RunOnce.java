package mod.chloeprime.apotheosismodernragnarok.common.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class RunOnce<P1, P2> implements BiConsumer<P1, P2> {
    public static <T1, T2> RunOnce<T1, T2> of(BiConsumer<T1, T2> action) {
        return new RunOnce<>(action);
    }

    public RunOnce(BiConsumer<P1, P2> action) {
        this.action = new AtomicReference<>(action);
    }

    @Override
    public void accept(P1 param1, P2 param2) {
        BiConsumer<P1, P2> block;
        if ((block = this.action.getAndSet(null)) != null) {
            block.accept(param1, param2);
        }
    }

    private final AtomicReference<BiConsumer<P1, P2>> action;
}
