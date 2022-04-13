package temps;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class TempSensor implements Runnable {

    static class TempReading implements Comparable<TempReading> {
        int temp;
        int timestamp;
        int sensorID;

        public TempReading(int temp, int timestamp, int sensorID) {
            this.temp = temp;
            this.timestamp = timestamp;
            this.sensorID = sensorID;
        }

        @Override
        public int compareTo(TempReading o) {
            int timeCmp = Integer.compare(timestamp, o.timestamp);
            if (timeCmp != 0)
                return timeCmp;
            int tempCmp = Integer.compare(temp, o.temp);
            if (tempCmp != 0) {
                return tempCmp;
            }
            return Integer.compare(sensorID, o.sensorID);
        }
    }

    ConcurrentSkipListSet<TempReading> readings = new ConcurrentSkipListSet<>();
    ConcurrentLinkedQueue<ConcurrentSkipListSet<TempReading>> hourlyReadingGroups;
    AtomicInteger numComplete = new AtomicInteger(0);
    AtomicInteger globalTimeStamp = new AtomicInteger(0);

    public TempSensor(ConcurrentLinkedQueue<ConcurrentSkipListSet<TempReading>> output) {
        this.hourlyReadingGroups = output;
    }

    @Override
    public void run() {
        int timestamp = 0;
        int sensorID = Integer.parseInt(Thread.currentThread().getName());
        while (true) {

            // Thread woke up before the last thread was able to complete its reading
            while (globalTimeStamp.get() != timestamp) {
                System.out.printf("%d %d%n", globalTimeStamp.get(), timestamp);
                Thread.onSpinWait();
            }

            // collect temp at regular intervals and store them in shared memory
            int reading = ThreadLocalRandom.current().nextInt(-100, 71);
            readings.add(new TempReading(reading, globalTimeStamp.get(), sensorID));
            timestamp++;
            if (timestamp >= 60)
                timestamp = 0;

            // If the thread is the last to complete, then increment the global counter
            if (numComplete.incrementAndGet() == Temps.THREAD_COUNT) {
                int val = globalTimeStamp.incrementAndGet();
                if (val >= 60) {
                    hourlyReadingGroups.add(readings);
                    readings = new ConcurrentSkipListSet<>();
                    numComplete.set(0);
                    globalTimeStamp.set(0);
                }
                numComplete.set(0);
            }

            try {
                // Sleep for 1 minute until next temp reading is required
                Thread.sleep((long) (60000 * Temps.TIME_SCALE));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
