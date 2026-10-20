package einfprog;

import java.util.Scanner;

/**
 * Reads a name and greets that person.
 */
public class Greeter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.println("Hello, " + name + "!");
    }
}
