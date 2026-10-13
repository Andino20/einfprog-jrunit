package plus.einfprog.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Tokenizer {

    private final String input;
    private int index;

    public Tokenizer(String input) {
        this.input = Objects.requireNonNull(input, "input must not be null");
        this.index = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        while ((t = nextToken()) != null)
            tokens.add(t);
        return tokens;
    }

    public Token nextToken() {
        char c = get();
        while (c != '\0') {
            if (Character.isLetter(c)) {
                int start = index;
                int length = 1;
                while (Character.isLetter(next())) {
                    length++;
                }
                return new Token(TokenType.WORD, start, length, input.substring(start, start + length));
            }

            if (isDigit(c) || isUnaryMinus()) {
                int start = index;
                do {
                    c = next();
                } while (isDigit(c));
                // a '.' only belongs to the number if digits follow, e.g. not at the end of a sentence
                if (c == '.' && isDigit(peek())) {
                    do {
                        c = next();
                    } while (isDigit(c));
                }
                return new Token(TokenType.NUMBER, start, index - start, input.substring(start, index));
            }

            if (!Character.isWhitespace(c)) {
                int start = index;
                next();
                return new Token(TokenType.WORD, start, 1, input.substring(start, start + 1));
            }

            c = next();
        }
        return null;
    }

    private boolean isUnaryMinus() {
        return get() == '-' && isDigit(peek())
                && (index == 0 || !Character.isLetterOrDigit(input.charAt(index - 1)));
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public char next() {
        return ++index < input.length() ? input.charAt(index) : '\0';
    }

    public char peek() {
        return index + 1 < input.length() ? input.charAt(index + 1) : '\0';
    }

    public char get() {
        return index < input.length() ? input.charAt(index) : '\0';
    }

}
