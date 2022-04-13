package temps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import temps.TempSensor.TempReading;

public class Temps {
    public static final int THREAD_COUNT = 8;
    public static final double TIME_SCALE = 1.0;

    public static void main(String[] args) {

        ConcurrentLinkedQueue<ConcurrentSkipListSet<TempReading>> hourlyReadingGroups = new ConcurrentLinkedQueue<>();
        startAndWaitForThreads(new TempSensor(hourlyReadingGroups));
        while (true) {
            while (hourlyReadingGroups.size() == 0)
                Thread.onSpinWait();

            // Perform Analysis on last hours Reading
            analyzeHourlyReading(hourlyReadingGroups.poll());
        }

    }

    private static void analyzeHourlyReading(ConcurrentSkipListSet<TempReading> poll) {
        Object[] arr = poll.toArray();

        ArrayList<Integer> maxes = new ArrayList<>();
        ArrayList<Integer> mins = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (i % 8 == 0) {
                mins.add(((TempReading) (arr[i])).temp);
            } else if (i != 0 && i % 7 == 0) {
                maxes.add(((TempReading) (arr[i])).temp);
            }
        }

        int tempDiff = 0;
        for (int i = 9; i < 60; i += 10) {
            int diff1 = Math.abs(maxes.get(i - 9) - mins.get(i));
            int diff2 = Math.abs(mins.get(i - 9) - maxes.get(i));
            int curDiff = Math.max(diff1, diff2);

            tempDiff = Math.max(tempDiff, curDiff);

        }

        maxes.sort(Collections.reverseOrder());
        Collections.sort(mins);

        System.out.println("############################################");
        System.out.print("Top 5: ");
        for (int i = 0; i < 5; i++) {
            System.out.printf("%d\t", maxes.get(i));

        }
        System.out.println();

        System.out.print("Low 5: ");
        for (int i = 0; i < 5; i++) {
            System.out.printf("%d\t", mins.get(i));

        }

        System.out.println();
        System.out.printf("Largest Temp. Diff. in 10min Interval:\t%d%n", tempDiff);
    }

    public static void startAndWaitForThreads(Runnable r) {
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread t = new Thread(r, "" + i);
            t.start();
            threads.add(t);
        }
        // for (Thread thread : threads) {
        // try {
        // thread.join();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        // }

    }
}
