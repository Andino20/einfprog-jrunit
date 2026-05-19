package plus.einfprog.pipeline.dto;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

public record MethodCall(@NonNull UUID id,
                         Object target,
                         Method method,
                         Object[] args) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCall that = (MethodCall) o;
        return id.equals(that.id) && target.equals(that.target) && method.equals(that.method) && Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + method.hashCode();
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    @NonNull
    public String toString() {
        return "MethodCall{" +
                "id=" + id +
                ", target=" + target +
                ", method=" + method +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
