package einfprog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.Settings;
import plus.einfprog.io.IOAssertions;
import plus.einfprog.io.Input;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.reflection.Reflected;

public class AverageTests {

    // numbers in the output may differ from the expected ones by up to 0.01, so "2.33" matches 2.3333333333333335
    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault()
            .withSettings(Settings.getDefault().withEpsilon(0.01));

    private static void runAverage() {
        // cast so the array is passed as the single String[] argument, not as varargs
        Reflected.on("einfprog.Average").call("main", (Object) new String[0]);
    }

    @Test
    void printsAverageAndMaximum() {
        Input in = Input.of(3, 1, 2, 4); // 3 numbers: 1, 2 and 4
        IOAssertions.prepareInput(in);
        runAverage();
        // line breaks don't matter and numbers are compared by value;
        // passing the input is optional and only shows it in the feedback if the test fails
        IOAssertions.assertOutputLoosely("""
                Average: 2.33
                Maximum: 4
                """, in);
    }

    @Test
    void onlyChecksTheAverage() {
        Input in = Input.of(3, 1, 2, 4);
        IOAssertions.prepareInput(in);
        runAverage();
        // anything printed after the expected output (here the maximum) is ignored
        IOAssertions.assertOutputLooselyTrimmed("Average: 2.33", in);
    }

    @Test
    void inputCanBeBuiltInALoop() {
        int[] numbers = {5, 10, 15, 20};
        Input.InputBuilder builder = Input.builder().add(numbers.length);
        for (int number : numbers) {
            builder.add(number);
        }
        Input in = builder.build();

        IOAssertions.prepareInput(in);
        runAverage();
        IOAssertions.assertOutputLoosely("Average: 12.5 Maximum: 20", in);
    }
}
