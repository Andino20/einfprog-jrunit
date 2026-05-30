
package plus.einfprog.util;

import java.util.AbstractList;
import java.util.List;

public class PrependedList<E> extends AbstractList<E> {

    private final E head;
    private final List<E> tail;

    public PrependedList(E head, List<E> tail) {
        if (tail == null)
            throw new IllegalArgumentException("tail should not be null");

        this.head = head;
        this.tail = tail;
    }

    @Override
    public int size() {
        return 1 + tail.size();
    }

    @Override
    public E get(int index) {
        if (index == 0) {
            return head;
        }
        return tail.get(index - 1);
    }

}