package plus.einfprog.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TokenizerTests {

    @Test
    void shouldTokenizeAtWhitespace() {
        String s = "Hellö my friend";
        List<Token> tokens = new Tokenizer(s).tokenize();
        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(new Token(TokenType.WORD, 0, 5, "Hellö"), tokens.get(0));
        Assertions.assertEquals(new Token(TokenType.WORD, 6, 2, "my"), tokens.get(1));
        Assertions.assertEquals(new Token(TokenType.WORD, 9, 6, "friend"), tokens.get(2));
    }

    @Test
    void shouldTokenizeSymbols() {
        String s = "a+b > c";
        List<Token> tokens = new Tokenizer(s).tokenize();
        Assertions.assertEquals(5, tokens.size());
        Assertions.assertEquals(new Token(TokenType.WORD, 0, 1, "a"), tokens.get(0));
        Assertions.assertEquals(new Token(TokenType.WORD, 1, 1, "+"), tokens.get(1));
        Assertions.assertEquals(new Token(TokenType.WORD, 2, 1, "b"), tokens.get(2));
        Assertions.assertEquals(new Token(TokenType.WORD, 4, 1, ">"), tokens.get(3));
        Assertions.assertEquals(new Token(TokenType.WORD, 6, 1, "c"), tokens.get(4));
    }

    @Test
    void shouldTokenizeNumbers() {
        List<Token> tokens = new Tokenizer("x = 10, y = 2.5").tokenize();
        Assertions.assertEquals(List.of(
                new Token(TokenType.WORD, 0, 1, "x"),
                new Token(TokenType.WORD, 2, 1, "="),
                new Token(TokenType.NUMBER, 4, 2, "10"),
                new Token(TokenType.WORD, 6, 1, ","),
                new Token(TokenType.WORD, 8, 1, "y"),
                new Token(TokenType.WORD, 10, 1, "="),
                new Token(TokenType.NUMBER, 12, 3, "2.5")), tokens);
    }

    @Test
    void shouldTokenizeNegativeNumbers() {
        Assertions.assertEquals(List.of("-3", "=", "-2.5", "(", "-1", ")"), values("-3 = -2.5 (-1)"));
    }

    @Test
    void shouldNotTreatBinaryMinusAsSign() {
        Assertions.assertEquals(List.of("10", "-", "3", "a", "-", "5", "7", "-", "2"), values("10-3 a-5 7 - 2"));
    }

    @Test
    void shouldSplitNumbersAndWords() {
        List<Token> tokens = new Tokenizer("100km").tokenize();
        Assertions.assertEquals(List.of(
                new Token(TokenType.NUMBER, 0, 3, "100"),
                new Token(TokenType.WORD, 3, 2, "km")), tokens);
    }

    @Test
    void shouldOnlyTreatDotAsDecimalPointBetweenDigits() {
        Assertions.assertEquals(List.of("It", "costs", "3", "."), values("It costs 3."));
        Assertions.assertEquals(List.of(".", "5"), values(".5"));
        Assertions.assertEquals(List.of("1.2", ".", "3"), values("1.2.3"));
    }

    @Test
    void shouldReturnEmptyListForEmptyString() {
        Assertions.assertEquals(List.of(), new Tokenizer("").tokenize());
    }

    @Test
    void shouldThrowOnNullInputString() {
        Assertions.assertThrows(NullPointerException.class, () -> new Tokenizer(null).tokenize());
    }

    private static List<String> values(String s) {
        return new Tokenizer(s).tokenize().stream().map(Token::value).toList();
    }

}
