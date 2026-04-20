package plus.einfprog;

import java.util.Arrays;
import java.util.Objects;

public interface Util {

    static Class<?>[] types(Object... args) {
        return Objects.nonNull(args) ?
                Arrays.stream(args)
                        .map(Object::getClass)
                        .toArray(Class<?>[]::new)
                : new Class<?>[0];
    }

}
