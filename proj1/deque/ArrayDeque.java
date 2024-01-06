package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    // Decrease the front pointer by 1 whenever we insert to front.
    private int front;
    // Increase the back pointer by 1 whenever we insert to back.
    private int back;
    private int size;
    private T[] items;
    private static final int INITIAL_CAPACITY = 8;
    private static final double LOAD_FACTOR_THRESHOLD = 0.25;
    private static final int MIN_LENGTH_FOR_RESIZE = 16;

    private class ArrayDequeIterator implements Iterator<T> {
        int size;
        int index;
        int counter;
        T[] dequeElements;
        ArrayDequeIterator(T[] i, int f, int s) {
            dequeElements = i;
            size = s;
            index = f;
            counter = 0;
        }

        @Override
        public boolean hasNext() {
            return counter < size;
        }
        @Override
        public T next() {
            T item = dequeElements[index];
            index = addOne(index);
            counter++;
            return item;
        }
    }

    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[INITIAL_CAPACITY];
        front = 0;
        back = 1;
    }

    private void resize(int newSize) {
        T[] newItems = (T[]) new Object[newSize];
        int idx = addOne(front);
        // copy items to new array
        for (int counter = 0; counter < size; counter++) {
            newItems[counter] = items[idx];
            idx = addOne(idx);
        }
        items = newItems;
        // reset pointers
        front = items.length - 1;
        back = size;
        if (back == items.length) {
            back = 0;
        }
    }

    /** Performs circular add. */
    private int addOne(int x) {
        if (x == items.length - 1) {
            return 0;
        }
        return x + 1;
    }

    /** Performs circular minus. */
    private int minusOne(int x) {
        if (x == 0) {
            return items.length - 1;
        }
        return x - 1;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        size++;
        items[front] = item;
        front = minusOne(front);
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        size++;
        items[back] = item;
        back = addOne(back);
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int idx = addOne(front);
        for (int counter = 0; counter < size; counter++) {
            System.out.print(items[idx] + " ");
            idx = addOne(idx);
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        double loadFactor = (double) size / items.length;
        if (items.length >= MIN_LENGTH_FOR_RESIZE && loadFactor <= LOAD_FACTOR_THRESHOLD) {
            resize(items.length / 4);
        }
        size--;
        front = addOne(front);
        T returnItem = items[front];
        // sets to null to avoid loitering
        items[front] = null;
        return returnItem;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        double loadFactor = (double) size / items.length;
        if (items.length >= MIN_LENGTH_FOR_RESIZE && loadFactor <= LOAD_FACTOR_THRESHOLD) {
            resize(items.length / 4);
        }
        size--;
        back = minusOne(back);
        T returnItem = items[back];
        // sets to null to avoid loitering
        items[back] = null;
        return returnItem;
    }

    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int realIndex = (front + index + 1) % items.length;
        return items[realIndex];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator(items, addOne(front), size);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        if (o instanceof LinkedListDeque) {
            LinkedListDeque<T> other = (LinkedListDeque<T>) o;
            // if not the same size, return
            if (size() != other.size()) {
                return false;
            }
            Iterator<T> thisIterator = iterator();
            Iterator<T> otherIterator = other.iterator();

            while (thisIterator.hasNext()) {
                T thisElement = thisIterator.next();
                T otherElement = otherIterator.next();
                if (!thisElement.equals(otherElement)) {
                    return false;
                }
            }
            return true;
        } else if (o instanceof ArrayDeque) {
            ArrayDeque<T> other = (ArrayDeque<T>) o;
            // if not the same size, return
            if (size() != other.size()) {
                return false;
            }
            Iterator<T> thisIterator = iterator();
            Iterator<T> otherIterator = other.iterator();

            while (thisIterator.hasNext()) {
                T thisElement = thisIterator.next();
                T otherElement = otherIterator.next();
                if (!thisElement.equals(otherElement)) {
                    return false;
                }
            }
            return true;
        }
        return false;

    }

}
