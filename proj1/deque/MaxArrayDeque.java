package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> currComparator;
    public MaxArrayDeque(Comparator<T> c) {
        currComparator = c;
    }

    /** returns the maximum element in the deque as governed by the previously given Comparator.
     * If the MaxArrayDeque is empty, simply return null. */
    public T max() {
        T curMax = get(0);
        for (T item: this) {
            if (currComparator.compare(item, curMax) > 0) {
                curMax = item;
            }
        }
        return curMax;
    }
    /** returns the maximum element in the deque as governed by the parameter Comparator c.
     * If the MaxArrayDeque is empty, simply return null. */
    public T max(Comparator<T> c) {
        T curMax = get(0);
        for (T item: this) {
            if (c.compare(item, curMax) > 0) {
                curMax = item;
            }
        }
        return curMax;
    }
}
