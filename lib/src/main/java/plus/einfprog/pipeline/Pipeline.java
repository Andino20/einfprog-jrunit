package plus.einfprog.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A generic implementation of a pipeline that stores an ordered list of hooks.
 * A hook is a function that takes an argument of type {@code T} and returns a value of type {@code T}
 * which is then passed to the next hook in the pipeline.
 * These hooks can either be added to the beginning or end of the pipeline.
 *
 * @param <T> The type of the argument and return value of a hook.
 * @param <F> The type of the hooks in the pipeline.
 */
public class Pipeline<T, F> {

    private final List<F> hooks = new ArrayList<>();
    private final Function<F, Function<T, T>> adapter;

    /**
     * Create a new pipeline with an adapter function that converts a hook to a {@link Function}.
     * The hooks are converted to follow a common interface and to provide access to methods like {@code Function::andThen}.
     * @param adapter Converts a hook to a {@code Function<T, T>}.
     */
    public Pipeline(Function<F, Function<T, T>> adapter) {
        this.adapter = adapter;
    }

    public void addLast(F hook) {
        this.hooks.add(hook);
    }

    public void addFirst(F hook) {
        this.hooks.addFirst(hook);
    }

    public T run(T t) {
        return hooks.stream()
                .map(adapter)
                .reduce(Function.identity(), Function::andThen)
                .apply(t);
    }

}
