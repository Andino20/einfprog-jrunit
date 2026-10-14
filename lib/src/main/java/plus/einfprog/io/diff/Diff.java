package plus.einfprog.io.diff;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

public class Diff<T> {

    @Getter
    private final List<Edit<T>> edits;

    public Diff(List<Edit<T>> edits) {
        this.edits = Objects.requireNonNull(edits, "edits must not be null");
    }

    public boolean hasMismatches() {
        return edits.stream().anyMatch(e -> e.type() != EditType.MATCH);
    }

    /**
     * Like {@link #hasMismatches()}, but ignores deletions at the very end, i.e. extra elements after
     * everything else matched.
     */
    public boolean hasMismatchesIgnoringTrailingDeletes() {
        return edits.reversed().stream()
                .dropWhile(e -> e.type() == EditType.DELETE)
                .anyMatch(e -> e.type() != EditType.MATCH);
    }
}
