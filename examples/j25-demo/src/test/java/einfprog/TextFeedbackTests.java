package einfprog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.io.IOAssertions;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.log.format.TextTraceFormatter;
import plus.einfprog.proxy.ProxyUtil;

/**
 * Failing tests that show the human-readable feedback of {@link TextTraceFormatter}.
 */
@Tag("failing")
public class TextFeedbackTests {

    @RegisterExtension
    static EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault()
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

}
