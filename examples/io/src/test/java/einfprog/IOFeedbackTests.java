package einfprog;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.io.IOAssertions;
import plus.einfprog.io.Input;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.reflection.Reflected;

/**
 * Failing tests that show the feedback for wrong console output.
 * Run them with {@code ./gradlew :examples:io:testFailing}.
 */
@Tag("failing")
public class IOFeedbackTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    private static void run(String className, Input in) {
        IOAssertions.prepareInput(in);
        Reflected.on(className).call("main", (Object) new String[0]);
    }

    @Test
    void wrongOutputTest() {
        run("einfprog.Greeter", Input.of("Alice"));
        IOAssertions.assertOutput("Hello, Bob!" + System.lineSeparator());
    }

    @Test
    void missingWordTest() {
        run("einfprog.Greeter", Input.of("Alice"));
        IOAssertions.assertOutputLoosely("Hello, dear Alice!");
    }

    @Test
    void unexpectedWordTest() {
        run("einfprog.Greeter", Input.of("Alice"));
        IOAssertions.assertOutputLoosely("Hello!");
    }

    @Test
    void replacedWordTest() {
        run("einfprog.Greeter", Input.of("Alice"));
        IOAssertions.assertOutputLoosely("Hello, Bob!");
    }

    @Test
    void wrongNumberWithInputTest() {
        Input in = Input.of(2, 1, 2);
        run("einfprog.Average", in);
        IOAssertions.assertOutputLoosely("Average: 2.0 Maximum: 2", in);
    }

    @Test
    void wrongSecondLineTest() {
        Input in = Input.of(2, 1, 2);
        run("einfprog.Average", in);
        IOAssertions.assertOutputLoosely("""
                Average: 1.5
                Maximum: 1
                """, in);
    }
}
