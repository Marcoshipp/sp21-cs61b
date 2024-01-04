package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> studentList = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solutionList = new ArrayDequeSolution<>();
        StringBuilder errorMessage = new StringBuilder();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int choice = StdRandom.uniform(0, 6);
            switch (choice) {
                case 0: {
                    int number = StdRandom.uniform(0, 100);
                    studentList.addFirst(number);
                    solutionList.addFirst(number);
                    errorMessage.append("addFirst" + "(" + number + ")" + "\n");
                    break;
                }
                case 1: {
                    int number = StdRandom.uniform(0, 100);
                    studentList.addLast(number);
                    solutionList.addLast(number);
                    errorMessage.append("addLast" + "(" + number + ")" + "\n");
                    break;
                }
                case 2: {
                    errorMessage.append("size" + "(" + ")" + "\n");
                    assertEquals(errorMessage.toString(), solutionList.size(), studentList.size());
                    break;
                }
                case 3: {
                    if (studentList.isEmpty() || solutionList.isEmpty()) continue;
                    errorMessage.append("removeFirst" + "(" + ")" + "\n");
                    assertEquals(errorMessage.toString(), solutionList.removeFirst(), studentList.removeFirst());
                    break;
                }
                case 4: {
                    if (studentList.isEmpty() || solutionList.isEmpty()) continue;
                    errorMessage.append("removeLast" + "(" + ")" + "\n");
                    assertEquals(errorMessage.toString(), solutionList.removeLast(), studentList.removeLast());
                    break;
                }
                case 5: {
                    if (studentList.isEmpty() || solutionList.isEmpty()) continue;
                    int number = StdRandom.uniform(0, solutionList.size());
                    errorMessage.append("get" + "(" + number + ")" + "\n");
                    assertEquals(errorMessage.toString(), solutionList.get(number), studentList.get(number));
                    break;
                }
                default:
                    break;
            }
        }
    }
}
