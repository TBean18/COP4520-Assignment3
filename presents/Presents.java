package presents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Presents {
    public static final int THREAD_COUNT = 4;
    private static final int PRESENT_COUNT = 500000;
    private static long startTime, stopTime;

    public static void main(String[] args) {
        List<Integer> presentBag = getBagOfPresents();
        Servant servants = new Servant(PRESENT_COUNT, presentBag);
        startAndWaitForThreads(servants);
        stopTime = System.currentTimeMillis();
        System.out.println("Confirmed Thank You's: " + servants.thankYouCards.get());
        System.out.println("Duration: " + (stopTime - startTime) + "ms");

    }

    public static List<Integer> getBagOfPresents() {
        List ret = new ArrayList<>();
        for (int i = 0; i < PRESENT_COUNT; i++) {
            ret.add(i);
        }
        Collections.shuffle(ret);
        return ret;

    }

    public static void startAndWaitForThreads(Runnable r) {
        ArrayList<Thread> threads = new ArrayList<>();
        startTime = System.currentTimeMillis();
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread t = new Thread(r, "" + i);
            t.start();
            threads.add(t);
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}