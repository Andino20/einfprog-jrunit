package plus.einfprog.io.diff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class DiffTests {

    @Test
    void diffExample() {
        // the example from the paper: LCS is CABA, so 4 matches + 5 modifications
        Assertions.assertEquals("-A -B =C +B =A =B -B =A +C", diff("ABCABBA", "CBABAC"));
    }

    @Test
    void identicalInputs() {
        Assertions.assertEquals("=A =B", diff("AB", "AB"));
    }

    @Test
    void leadingMatch() {
        Assertions.assertEquals("=A =B -C +D", diff("ABC", "ABD"));
    }

    @Test
    void leadingDifference() {
        Assertions.assertEquals("-X =A =B", diff("XAB", "AB"));
    }

    @Test
    void noMatch() {
        Assertions.assertEquals("-A +B", diff("A", "B"));
    }

    @Test
    void emptyActual() {
        Assertions.assertEquals("-A", diff("A", ""));
    }

    @Test
    void emptyExpected() {
        Assertions.assertEquals("+A", diff("", "A"));
    }

    // Diffs the characters of a and b and renders the edit script as e.g. "-A =B +C" (- delete, + insert, = match).
    private static String diff(String a, String b) {
        List<Edit<Character>> edits = new MyersAlgorithm<>(Character::equals)
                .computeDiff(chars(a), chars(b))
                .getEdits();
        return edits.stream()
                .map(edit -> symbol(edit.type()) + edit.value())
                .collect(Collectors.joining(" "));
    }

    private static List<Character> chars(String s) {
        return s.chars().mapToObj(c -> (char) c).toList();
    }

    private static String symbol(EditType type) {
        return switch (type) {
            case DELETE -> "-";
            case INSERT -> "+";
            case MATCH -> "=";
        };
    }

}
