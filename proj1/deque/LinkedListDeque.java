package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private final class Node {
        private Node prev;
        private T item;
        private Node next;

        private Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        Node iterS;
        int counter;
        int size;
        LinkedListDequeIterator(Node s, int sze) {
            iterS = s.next;
            counter = 0;
            size = sze;
        }

        @Override
        public boolean hasNext() {
            return counter < size;
        }
        @Override
        public T next() {
            T item = iterS.item;
            iterS = iterS.next;
            counter++;
            return item;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    private T getLast() {
        if (size == 0) {
            return null;
        }
        return sentinel.prev.item;
    }

    @Override
    public void addFirst(T item) {
        size++;
        Node newItem = new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = newItem;
        sentinel.next = newItem;
    }

    @Override
    public void addLast(T item) {
        size++;
        Node newItem = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = newItem;
        sentinel.prev = newItem;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size--;
        Node curFirst = sentinel.next;
        curFirst.next.prev = sentinel;
        sentinel.next = curFirst.next;
        return curFirst.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size--;
        Node curLast = sentinel.prev;
        curLast.prev.next = sentinel;
        sentinel.prev = curLast.prev;
        return curLast.item;
    }

    @Override
    public T get(int i) {
        if (i >= size || i < 0) {
            return null;
        }
        if (i == size - 1) {
            return getLast();
        }
        Node ptr = sentinel.next;
        while (i > 0) {
            ptr = ptr.next;
            i--;
        }
        return ptr.item;
    }

    public T getRecursive(int i) {
        if (i >= size || i < 0) {
            return null;
        }
        if (i == size - 1) {
            return getLast();
        }
        return get(this.sentinel.next, i);
    }

    private T get(Node p, int index) {
        if (index == 0) {
            return p.item;
        }
        return get(p.next, index - 1);
    }

    @Override
    public void printDeque() {
        Node ptr = sentinel.next;
        int index = 0;
        while (index < size) {
            System.out.print(ptr.item + " ");
            ptr = ptr.next;
            index++;
        }
        System.out.println();
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator(sentinel, size);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> lld = (Deque<?>) o;
        if (lld.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (lld.get(i) != get(i)) {
                return false;
            }
        }
        return true;
    }
}
