package einfprog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.io.IOAssertions;
import plus.einfprog.io.Input;
import plus.einfprog.junit.EinfprogJRunitExtension;
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
            .withFormatter(TextTraceFormatter::new);

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
    void wrongOutputTest() {
        ProgramProxy proxy = ProxyUtil.create(ProgramProxy.class);
        proxy.helloWorld();
        IOAssertions.assertOutput("Hello Java!" + System.lineSeparator());
    }

    @Test
    void missingWordInOutputTest() {
        ProgramProxy proxy = ProxyUtil.create(ProgramProxy.class);
        proxy.helloWorld();
        IOAssertions.assertOutputLoosely("Hello Java World!");
    }

    @Test
    void unexpectedWordInOutputTest() {
        ProgramProxy proxy = ProxyUtil.create(ProgramProxy.class);
        proxy.helloWorld();
        IOAssertions.assertOutputLoosely("Hello!");
    }

    @Test
    void wrongNumberInOutputTest() {
        ProgramProxy proxy = ProxyUtil.create(ProgramProxy.class);
        IO.println("2 + 2 = " + proxy.add(2, 2));
        IOAssertions.assertOutputLoosely("2 + 2 = 5");
    }

    @Test
    void wordReplacedInOutputTest() {
        IO.println("Hello James!");
        IOAssertions.assertOutputLoosely("Hello Sam!");
    }

    @Test
    void wrongOutputWithInputTest() {
        Input in = Input.of(2.0, 3.0);
        IO.println("2.0 + 3.0 = 4.0");
        IOAssertions.assertOutputLoosely("2.0 + 3.0 = 5.0", in);
    }

    @Test
    void wrongOutputWithMultipleLines() {
        IO.println("This is line 1\nFollowed by line 2\nHere an error");
        IOAssertions.assertOutputLoosely("This is line 1\nFollowed by line 2\nHere is an error");
    }

    @Test
    void missingMethodTest() {
        Reflected.on("Program").call("subtract", 2, 1);
    }

    @Test
    void missingClassTest() {
        Reflected.on("Calculator").create();
    }

}
