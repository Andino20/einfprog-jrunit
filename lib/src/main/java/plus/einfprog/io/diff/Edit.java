package plus.einfprog.io.diff;

public record Edit<T>(T value,
                      EditType type) {
}
