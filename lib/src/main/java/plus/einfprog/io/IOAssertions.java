package plus.einfprog.io;

import plus.einfprog.EinfprogJRunit;
import plus.einfprog.exception.OutputMismatchException;
import plus.einfprog.io.diff.Diff;
import plus.einfprog.io.diff.MyersAlgorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Utility class providing assertion and setup methods for testing console input/output (I/O).
 */
public final class IOAssertions {

    public static void prepareInput(Input input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.toString().getBytes(StandardCharsets.UTF_8));
        System.setIn(in);
    }

    public static void assertOutput(String expected) {
        assertOutput(expected, null);
    }

    public static void assertOutput(String expected, Input input) {
        String s = consumeOutputBuffer();
        if (!s.equals(expected)) {
            throw OutputMismatchException.builder()
                    .actual(s)
                    .expected(expected)
                    .input(input)
                    .message("Output was not as expected")
                    .build();
        }
    }

    public static void assertOutputLoosely(String expected) {
        assertOutputLoosely(expected, null);
    }

    public static void assertOutputLoosely(String expected, Input input) {
        String actual = consumeOutputBuffer();

        List<Token> expectedTokens = new Tokenizer(expected).tokenize();
        List<Token> actualTokens = new Tokenizer(actual).tokenize();

        double epsilon = EinfprogJRunit.getContext().settings().getEpsilon();
        Diff<Token> diff = new MyersAlgorithm<Token>((a, e) -> Token.matches(a, e, epsilon))
                .computeDiff(actualTokens, expectedTokens);
        // extra output after the expected output is allowed
        if (diff.hasMismatchesIgnoringTrailingDeletes()) {
            throw OutputMismatchException.builder()
                    .actual(actual)
                    .expected(expected)
                    .input(input)
                    .diff(diff)
                    .message("Output was not as expected")
                    .build();
        }
    }

    private static String consumeOutputBuffer() {
        ByteArrayOutputStream out = EinfprogJRunit.getContext().out();
        String s = out.toString(StandardCharsets.UTF_8);
        out.reset();
        return s;
    }

}
