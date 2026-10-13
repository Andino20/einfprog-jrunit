package plus.einfprog.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.Settings;
import plus.einfprog.exception.OutputMismatchException;
import plus.einfprog.junit.EinfprogJRunitExtension;

public class LooseOutputEpsilonTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault()
            .withSettings(Settings.getDefault().withEpsilon(0.01));

    @Test
    void numbersWithinEpsilonMatch() {
        IO.println("pi = 3.14");
        IOAssertions.assertOutputLoosely("pi = 3.14159");
    }

    @Test
    void numbersOutsideEpsilonDoNotMatch() {
        IO.println("pi = 3.2");
        Assertions.assertThrows(OutputMismatchException.class, () -> IOAssertions.assertOutputLoosely("pi = 3.14159"));
    }

}
