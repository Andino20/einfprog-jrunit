package plus.einfprog.io;

public record Token(TokenType type,
                    int index,
                    int length,
                    String value) {

    static boolean matches(Token actual, Token expected, double epsilon) {
        if (actual.type() != expected.type())
            return false;
        return switch (actual.type()) {
            case WORD -> actual.value().equals(expected.value());
            case NUMBER ->
                    Math.abs(Double.parseDouble(actual.value()) - Double.parseDouble(expected.value())) <= epsilon;
        };
    }

}
