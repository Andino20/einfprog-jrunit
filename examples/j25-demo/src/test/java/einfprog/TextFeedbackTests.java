package einfprog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.assertion.ArrayAssertions;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.log.format.HumanReadableTraceFormatter;
import plus.einfprog.log.format.TextTraceFormatter;
import plus.einfprog.proxy.ProxyUtil;
import plus.einfprog.reflection.Reflected;

/**
 * Failing tests that show the human-readable feedback of {@link TextTraceFormatter}.
 */
@Tag("failing")
public class TextFeedbackTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault()
            .withFormatter(HumanReadableTraceFormatter::new);

    @Test
    void wrongResultTest() {
        ProgramProxy proxy = ProxyUtil.create(ProgramProxy.class);
        Assertions.assertEquals(42, proxy.add(21, 21));
        Assertions.assertEquals(5, proxy.add(2, 2));
    }

    @Test
    void exceptionTest() throws Throwable {
        ProgramProxy proxy = ProxyUtil.create(ProgramProxy.class);
        proxy.thisThrows(new IllegalStateException("something broke"));
    }

    @Test
    void missingMethodTest() {
        Reflected.on("Program").call("subtract", 2, 1);
    }

    @Test
    void missingClassTest() {
        Reflected.on("Calculator").create();
    }

    @Test
    void arrayTest() {
        int[] a = {1, 2, 3};
        int[] b = {3, 2, 1};

        int[] c = ProxyUtil.create(ProgramProxy.class).addVector(a, b);
        ArrayAssertions.assertArrayEquals(new int[]{1, 1, 1}, c, "Wrong array");
    }

    @Test
    void multiDimensionalArrayTest() {
        boolean[][] graph = {{true, false}, {false, true}};
        boolean[][] graph2 = ProxyUtil.create(ProgramProxy.class).getGraphBack(graph);
        ArrayAssertions.assertArrayEquals(new boolean[][]{{false, true}, {true, false}}, graph2, "Wrong graph");
    }

}
