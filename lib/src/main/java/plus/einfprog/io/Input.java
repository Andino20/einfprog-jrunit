package plus.einfprog.io;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents structured console input for testing purposes.
 * <p>
 * This class collects a sequence of input values in the form of {@code Object}s
 * and formats them into a single newline-separated {@link String} via {@link #toString()},
 * mimicking multi-line user input typed into standard input ({@code System.in}).
 * </p>
 */
public class Input {
    private final List<Object> inputs;

    private Input() {
        inputs = new ArrayList<>();
    }

    public static InputBuilder builder() {
        Input input = new Input();
        return new InputBuilder(input);
    }

    public static Input of(Object... inputs) {
        InputBuilder builder = builder();
        for (Object o : inputs) {
            builder.add(o);
        }
        return builder.build();
    }

    public static class InputBuilder {
        private final Input in;

        InputBuilder(Input in) {
            this.in = in;
        }

        public InputBuilder add(Object input) {
            if (input == null)
                throw new IllegalArgumentException("input cannot be null");
            in.inputs.add(input);
            return this;
        }

        public Input build() {
            return in;
        }
    }

    @Override
    public String toString() {
        return String.join("\n", inputs.stream().map(Object::toString).toList());
    }
}
