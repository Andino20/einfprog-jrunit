package plus.einfprog.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Pipeline<T, F> {

    private final List<F> hooks = new ArrayList<>();
    private final Function<F, Function<T, T>> adapter;

    public Pipeline(Function<F, Function<T, T>> adapter) {
        this.adapter = adapter;
    }

    public void add(F hook) {
        this.hooks.add(hook);
    }

    public T run(T t) {
        return hooks.stream()
                .map(adapter)
                .reduce(Function.identity(), Function::andThen)
                .apply(t);
    }

}
