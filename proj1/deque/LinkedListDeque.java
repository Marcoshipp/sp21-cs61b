package deque;

public class LinkedListDeque<T> implements Deque<T> {
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
    public boolean isEmpty() {
        return size == 0;
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

}
