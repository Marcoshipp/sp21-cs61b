package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> BuggyL = new BuggyAList<>();
        boolean passed = true;
        for (int i = 0; i < 3; i++) {
            L.addLast(i);
            BuggyL.addLast(i);
        }
        for (int i = 0; i < 3; i++) {
            passed &= L.removeLast() == BuggyL.removeLast();
        }
        assertEquals(true, passed);
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> BuggyL = new BuggyAList<>();
        int N = 5000;
        boolean passed = true;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            switch (operationNumber) {
                case 0: {
                    int randVal = StdRandom.uniform(0, 100);
                    L.addLast(randVal);
                    BuggyL.addLast(randVal);
                    break;
                }

                case 1: {
                    int sizeL = L.size();
                    int sizeBuggyL = BuggyL.size();
                    passed &= sizeL == sizeBuggyL;
                    break;
                }

                case 2: {
                    if (L.size() > 0 && BuggyL.size() > 0) {
                        int last1 = L.getLast();
                        int last2 = BuggyL.getLast();
                        passed &= last1 == last2;
                    }
                    break;
                }

                case 3: {
                    if (L.size() > 0 && BuggyL.size() > 0) {
                        int removed1 = L.removeLast();
                        int removed2 = BuggyL.removeLast();
                        passed &= removed1 == removed2;
                    }
                    break;
                }

                case 4: {
                    int randVal = StdRandom.uniform(0, L.size());
                    int return1 = L.get(randVal);
                    int return2 = BuggyL.get(randVal);
                    passed &= return1 == return2;
                    break;
                }

                default:
                    break;
            }
        }
        assertEquals(true, passed);
    }
}
