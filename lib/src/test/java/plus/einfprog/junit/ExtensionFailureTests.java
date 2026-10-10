package plus.einfprog.junit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

public class ExtensionFailureTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @Test
    void failureShouldKeepOriginalExceptionAsCause() {
        IllegalStateException original = new IllegalStateException("something broke");

        AssertionFailedError error = Assertions.assertThrows(AssertionFailedError.class,
                () -> einfprogJrunit.handleTestExecutionException(null, original));

        Assertions.assertSame(original, error.getCause());
        Assertions.assertTrue(error.getMessage().contains("something broke"));
    }

    @Test
    void failureShouldKeepExpectedAndActualValues() {
        AssertionFailedError original = Assertions.assertThrows(AssertionFailedError.class,
                () -> Assertions.assertEquals(5, 4));

        AssertionFailedError error = Assertions.assertThrows(AssertionFailedError.class,
                () -> einfprogJrunit.handleTestExecutionException(null, original));

        Assertions.assertSame(original, error.getCause());
        Assertions.assertEquals(5, error.getExpected().getValue());
        Assertions.assertEquals(4, error.getActual().getValue());
    }

    @Test
    void abortedTestShouldBeRethrownUntouched() {
        TestAbortedException aborted = new TestAbortedException("assumption failed");

        TestAbortedException thrown = Assertions.assertThrows(TestAbortedException.class,
                () -> einfprogJrunit.handleTestExecutionException(null, aborted));

        Assertions.assertSame(aborted, thrown);
    }

    @Test
    void failedAssumptionShouldSkipTheTest() {
        Assumptions.assumeTrue(false, "this test must be reported as skipped, not failed");
    }
}
