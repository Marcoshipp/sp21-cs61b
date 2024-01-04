package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<>();
        AList<Integer> Ms = new AList<>();
        int test = 8;
        for (int i = 0; i < test; i++) {
            Ns.addLast(1000 * (int) Math.pow(2, i));
            Ms.addLast(10000);
        }
        AList<Double> times = new AList<>();
        for (int i = 0; i < test; i++) {
            SLList<Integer> demo = new SLList<>();
            for (int j = 0; j < Ns.get(i); j++) {
                demo.addLast(j);
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < Ms.get(i); j++) {
                demo.getLast();
            }
            double elapsedTime = sw.elapsedTime();
            times.addLast(elapsedTime);
        }
        printTimingTable(Ns, times, Ms);
    }

}
