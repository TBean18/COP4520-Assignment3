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

    public TempSensor(ConcurrentLinkedQueue<ConcurrentSkipListSet<TempReading>> output) {
        this.hourlyReadingGroups = output;
    }

    @Override
    public void run() {
        int timestamp = 0;
        int sensorID = Integer.parseInt(Thread.currentThread().getName());
        while (true) {

            // collect temp at regular intervals and store them in shared memory
            int reading = ThreadLocalRandom.current().nextInt(-100, 71);
            readings.add(new TempReading(reading, timestamp, sensorID));
            timestamp++;
            if (timestamp >= 60) {
                timestamp = 0;
                int complete = numComplete.incrementAndGet();
                if (complete == 8) {
                    hourlyReadingGroups.add(readings);
                    readings = new ConcurrentSkipListSet<>();
                    numComplete.set(0);
                } else
                    while (numComplete.get() != 0) {
                        Thread.onSpinWait();
                    }
            }

            try {
                Thread.sleep((long) (1000 * Temps.TIME_SCALE));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
