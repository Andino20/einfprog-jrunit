package plus.einfprog.assertion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class ArrayAssertionsTests {

    @Test
    void equalArraysPassTest() {
        ArrayAssertions.assertArrayEquals(new int[]{1, 2, 3}, new int[]{1, 2, 3});
        ArrayAssertions.assertArrayEquals(new int[][]{{1}, {2, 3}}, new int[][]{{1}, {2, 3}});
    }

    @Test
    void mismatchReportsWholeArraysTest() {
        AssertionFailedError e = Assertions.assertThrows(AssertionFailedError.class,
                () -> ArrayAssertions.assertArrayEquals(new int[]{1, 2, 3}, new int[]{1, 5, 3}, "wrong"));

        Assertions.assertEquals("[1, 2, 3]", e.getExpected().getStringRepresentation());
        Assertions.assertEquals("[1, 5, 3]", e.getActual().getStringRepresentation());
    }

    @Test
    void nestedMismatchReportsWholeArraysTest() {
        AssertionFailedError e = Assertions.assertThrows(AssertionFailedError.class,
                () -> ArrayAssertions.assertArrayEquals(new int[][]{{1}, {2}}, new int[][]{{1}, {3}}));

        Assertions.assertEquals("[[1], [2]]", e.getExpected().getStringRepresentation());
        Assertions.assertEquals("[[1], [3]]", e.getActual().getStringRepresentation());
    }

}
