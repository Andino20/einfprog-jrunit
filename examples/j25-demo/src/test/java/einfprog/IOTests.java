package einfprog;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.io.IOAssertions;
import plus.einfprog.junit.EinfprogJRunitExtension;

public class IOTests {

    @RegisterExtension
    static EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @Test
    void assertConsoleOutputTest() {
        IO.println("Foo");
        IOAssertions.assertOutput("Foo\n");
    }

    @Test
    @Tag("failing")
    void feedbackConsoleOutputTest() {
        IO.println("Foo");
        IOAssertions.assertOutput("Bar\n");
    }

}
