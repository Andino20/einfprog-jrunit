package einfprog;

import java.util.Scanner;

/**
 * Reads how many numbers follow, then the numbers themselves, and prints their average and maximum.
 */
public class Average {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        int sum = 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int number = scanner.nextInt();
            sum += number;
            max = Math.max(max, number);
        }

        System.out.println("Average: " + (double) sum / n);
        System.out.println("Maximum: " + max);
    }
}
