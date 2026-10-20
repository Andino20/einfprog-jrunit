package einfprog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.io.IOAssertions;
import plus.einfprog.io.Input;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.reflection.Reflected;

public class GreeterTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    private static void runGreeter() {
        // cast so the array is passed as the single String[] argument, not as varargs
        Reflected.on("einfprog.Greeter").call("main", (Object) new String[0]);
    }

    @Test
    void greetsByName() {
        IOAssertions.prepareInput(Input.of("Alice"));
        runGreeter();
        // assertOutput compares exactly, including the line break
        IOAssertions.assertOutput("Hello, Alice!" + System.lineSeparator());
    }

    @Test
    void greetsEveryoneInTurn() {
        // every assertion clears the captured output, so the second one only sees the second greeting
        IOAssertions.prepareInput(Input.of("Alice"));
        runGreeter();
        IOAssertions.assertOutput("Hello, Alice!" + System.lineSeparator());

        IOAssertions.prepareInput(Input.of("Bob"));
        runGreeter();
        IOAssertions.assertOutput("Hello, Bob!" + System.lineSeparator());
    }

    @Test
    void looseComparisonIgnoresWhitespace() {
        IOAssertions.prepareInput(Input.of("Alice"));
        runGreeter();
        // whitespace (including line breaks) is ignored, only words, numbers and symbols are compared
        IOAssertions.assertOutputLoosely("Hello, Alice!");
    }
}
