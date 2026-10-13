package plus.einfprog.io.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

public final class MyersAlgorithm<T> {

    private final BiPredicate<? super T, ? super T> equality;

    public MyersAlgorithm(BiPredicate<? super T, ? super T> equality) {
        Objects.requireNonNull(equality, "equality must not be null");
        this.equality = equality;
    }

    public Diff<T> computeDiff(List<T> a, List<T> b) {
        EditGraphNode endNode = computeTrace(a, b);
        return new Diff<>(backtrack(a, b, endNode));
    }

    private EditGraphNode computeTrace(List<T> a, List<T> b) {
        Objects.requireNonNull(a, "a must not be null");
        Objects.requireNonNull(b, "b must not be null");

        int n = a.size();
        int m = b.size();

        int max = n + m;
        if (max == 0)
            return new EditGraphNode(1, 0, null);

        EditGraphNode[] v = new EditGraphNode[2 * max + 1];
        int middle = (2 * max + 1) / 2;

        v[middle + 1] = new EditGraphNode(1, 0, null);
        for (int d = 0; d <= max; d++) {
            for (int k = -d; k <= d; k += 2) {
                EditGraphNode prev;
                int x;
                if (k == -d || (k != d && v[middle + k - 1].x() < v[middle + k + 1].x())) {
                    x = v[middle + k + 1].x();
                    prev = v[middle + k + 1];
                } else {
                    x = v[middle + k - 1].x() + 1;
                    prev = v[middle + k - 1];
                }
                int y = x - k;

                while (x < n && y < m && equality.test(a.get(x), b.get(y))) {
                    x++;
                    y++;
                }
                v[middle + k] = new EditGraphNode(k, x, prev);

                if (x >= n && y >= m)
                    return v[middle + k];
            }
        }

        throw new IllegalStateException("unreachable");
    }

    private List<Edit<T>> backtrack(List<T> a, List<T> b, EditGraphNode endNode) {
        List<Edit<T>> edits = new ArrayList<>();
        int x = endNode.x();
        int y = x - endNode.k();

        EditGraphNode current = endNode;
        while (current.prev() != null) {
            EditGraphNode prev = current.prev();
            int prevX = prev.x();
            int prevY = prevX - prev.k();

            while (x > prevX && y > prevY) {
                x--;
                y--;
                edits.add(new Edit<>(a.get(x), EditType.MATCH));
            }

            if (prev.prev() != null) {
                if (x == prevX) {
                    edits.add(new Edit<>(b.get(prevY), EditType.INSERT));
                } else {
                    edits.add(new Edit<>(a.get(prevX), EditType.DELETE));
                }
                x = prevX;
                y = prevY;
            }
            current = prev;
        }
        return edits.reversed();
    }
}