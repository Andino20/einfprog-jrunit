package plus.einfprog.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

import java.lang.IO;

public class IOTests {

    @RegisterExtension
    private final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @Test
    void outputTest() {
        IO.println("Hello World!");
        IOAssertions.assertOutput("Hello World!\n");

        IO.println("My first Java program");
        IOAssertions.assertOutput("My first Java program\n");
    }

    @Test
    void inputTest() {
        IOAssertions.prepareInput(Input.of("Hello world!", 42));

        String text = IO.readln();
        Assertions.assertEquals("Hello world!", text);

        int x = Integer.parseInt(IO.readln());
        Assertions.assertEquals(42, x);
    }

}
