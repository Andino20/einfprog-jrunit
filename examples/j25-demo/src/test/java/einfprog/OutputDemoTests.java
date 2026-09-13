package einfprog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.io.IOAssertions;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.proxy.ProxyUtil;

public class OutputDemoTests {

    @RegisterExtension
    static EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @Test
    @Tag("failing")
    void feedbackAssertionTest() {
        Assertions.assertEquals(5, 4);
    }

    @Test
    @Tag("failing")
    void feedbackConsoleOutputTest() {
        IO.println("Foo");
        IOAssertions.assertOutput("Bar\n");
    }

    @Test
    @Tag("failing")
    void feedbackTargetInvocationTest() throws Throwable {
        ProgramProxy program = ProxyUtil.create(ProgramProxy.class);
        program.thisThrows(new NullPointerException());
    }


}
