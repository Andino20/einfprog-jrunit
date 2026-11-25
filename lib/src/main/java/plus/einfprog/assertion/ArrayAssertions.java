package plus.einfprog.assertion;

import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.ValueWrapper;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Array assertions that behave like JUnit's {@link Assertions#assertArrayEquals}, but report the whole arrays.
 *
 * <p>JUnit only puts the mismatching elements (or the differing lengths) into the {@link AssertionFailedError}.
 * These methods run the JUnit assertion and, if it fails, rethrow an {@link AssertionFailedError} with the same
 * message (which still names the mismatching index) whose expected and actual values are the complete arrays,
 * rendered with {@link Arrays#toString} or, for object arrays, {@link Arrays#deepToString(Object[])}.
 * The original error is its cause.
 *
 * <p>Multi-dimensional arrays use the {@code Object[]} overloads, since e.g. an {@code int[][]} is an
 * {@code Object[]}. JUnit compares them deeply, but without a tolerance for nested floating point values.
 */
public final class ArrayAssertions {

    private ArrayAssertions() {
    }

    public static void assertArrayEquals(boolean[] expected, boolean[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(boolean[] expected, boolean[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(char[] expected, char[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(char[] expected, char[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(byte[] expected, byte[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(short[] expected, short[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(short[] expected, short[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(int[] expected, int[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(int[] expected, int[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(long[] expected, long[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(long[] expected, long[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(float[] expected, float[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(float[] expected, float[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(float[] expected, float[] actual, float delta) {
        assertArrayEquals(expected, actual, delta, null);
    }

    public static void assertArrayEquals(float[] expected, float[] actual, float delta, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, delta, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(double[] expected, double[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(double[] expected, double[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(double[] expected, double[] actual, double delta) {
        assertArrayEquals(expected, actual, delta, null);
    }

    public static void assertArrayEquals(double[] expected, double[] actual, double delta, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, delta, message), expected, actual, Arrays::toString);
    }

    public static void assertArrayEquals(Object[] expected, Object[] actual) {
        assertArrayEquals(expected, actual, null);
    }

    public static void assertArrayEquals(Object[] expected, Object[] actual, String message) {
        withFullArrays(() -> Assertions.assertArrayEquals(expected, actual, message), expected, actual, Arrays::deepToString);
    }

    private static <T> void withFullArrays(Runnable assertion, T expected, T actual, Function<T, String> toString) {
        try {
            assertion.run();
        } catch (AssertionFailedError e) {
            throw new AssertionFailedError(e.getMessage(),
                    ValueWrapper.create(expected, toString.apply(expected)),
                    ValueWrapper.create(actual, toString.apply(actual)),
                    e);
        }
    }

}
