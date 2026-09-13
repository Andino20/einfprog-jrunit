package plus.einfprog.io;

import plus.einfprog.EinfprogJRunit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public final class IOAssertions {

    public static void prepareInput(Input input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.toString().getBytes());
        System.setIn(in);
    }

    public static void assertOutput(String expected) {
        String s = consumeOutputBuffer();
        if (!s.equals(expected)) {
            throw EinfprogJRunitAssertionError.builder()
                    .actual(s)
                    .expected(expected)
                    .message("Output was not as expected")
                    .build();
        }
    }

    private static String consumeOutputBuffer() {
        ByteArrayOutputStream out = EinfprogJRunit.getContext().out();
        String s = out.toString();
        out.reset();
        return s;
    }

}
