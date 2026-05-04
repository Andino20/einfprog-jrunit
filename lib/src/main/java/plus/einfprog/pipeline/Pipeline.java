package plus.einfprog.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Pipeline<T> {

    private final List<Function<T, T>> hooks = new ArrayList<>();

    public void add(Function<T, T> hook) {
        this.hooks.add(hook);
    }

    public T run(T t) {
        return hooks.stream()
                .reduce(Function.identity(), Function::andThen)
                .apply(t);
    }

}
