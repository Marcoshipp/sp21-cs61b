package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class DequeTest {
    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<String>();
        assertTrue("A newly initialized LLDeque should be empty", ad1.isEmpty());

        ad1.addFirst("front");
        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String>  ad1 = new ArrayDeque<String>();
        ArrayDeque<Double>  ad2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigALDequeTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }
    }
    @Test
    public void testThreeAddThreeRemove() {
        ArrayDeque<Integer> L1 = new ArrayDeque<>();
        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();
        boolean passed = true;
        for (int i = 0; i < 3; i++) {
            L1.addLast(i);
            L2.addLast(i);
        }
        for (int i = 0; i < 3; i++) {
            passed &= L1.removeLast() == L2.removeLast();
        }
        assertTrue(passed);
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> L1 = new ArrayDeque<>();
        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();
        int N = 5000;
        boolean passed = true;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            switch (operationNumber) {
                case 0: {
                    int randVal = StdRandom.uniform(0, 100);
                    L1.addLast(randVal);
                    L2.addLast(randVal);
                    break;
                }

                case 1: {
                    int randVal = StdRandom.uniform(0, 100);
                    L1.addFirst(randVal);
                    L2.addFirst(randVal);
                    break;
                }

                case 2: {
                    int sizeL = L1.size();
                    int sizeL2 = L2.size();
                    passed &= sizeL == sizeL2;
                    assertEquals("L1 and L2 has different size, something's off with adding", true, passed);
                    break;
                }
                
                case 3: {
                    if (L1.size() > 0 && L2.size() > 0) {
                        int removed1 = L1.removeLast();
                        int removed2 = L2.removeLast();
                        passed &= removed1 == removed2;
                        assertEquals("L1 and L2 has different return, occurred in removeLast testing", true, passed);
                    }
                    assertEquals("L1 and L2 has different size, something's off with adding", true, passed);
                    break;
                }

                case 4: {
                    if (L1.size() == 0) continue;
                    int randVal = StdRandom.uniform(0, L1.size());
                    int return1 = L1.get(randVal);
                    int return2 = L2.get(randVal);
                    passed &= return1 == return2;
                    assertEquals("L1 and L2 has different return, occurred in get testing", true, passed);
                    break;
                }

                default:
                    break;
            }
        }
    }

    @Test
    public void equalTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        LinkedListDeque<Integer> ld = new LinkedListDeque<>();
        ArrayDeque<String> spyAd = new ArrayDeque<>();
        LinkedListDeque<String> spyLd = new LinkedListDeque<>();
        spyAd.addLast("61b");
        spyLd.addLast("rules");
        for (int i = 0; i < 50; i++) {
            ad.addLast(i);
            ld.addLast(i);
        }
        assertTrue(ad.equals(ld));
        assertTrue(ld.equals(ad));
        assertFalse(ad.equals(spyAd));
        assertFalse(ad.equals(spyLd));
        assertFalse(ld.equals(spyAd));
        assertFalse(spyLd.equals(ad));
    }

}
