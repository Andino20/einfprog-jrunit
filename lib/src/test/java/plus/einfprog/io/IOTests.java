package plus.einfprog.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

import java.lang.IO;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class IOTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @Test
    void outputTest() {
        IO.println("Hello World!");
        IOAssertions.assertOutput("Hello World!" + System.lineSeparator());

        IO.println("My first Java program");
        IOAssertions.assertOutput("My first Java program" + System.lineSeparator());
    }

    @Test
    void inputTest() {
        IOAssertions.prepareInput(Input.of("Hello world!", 42));

        String text = IO.readln();
        Assertions.assertEquals("Hello world!", text);

        int x = Integer.parseInt(IO.readln());
        Assertions.assertEquals(42, x);
    }

    @Test
    void scannerInputTest() {
        IOAssertions.prepareInput(Input.of("Hello world!", 42));

        try (var scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            Assertions.assertEquals("Hello world!", scanner.nextLine());
            Assertions.assertEquals(42, scanner.nextInt());
        }
    }

}
