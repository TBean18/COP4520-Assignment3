package temps;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import temps.TempSensor.TempReading;

public class Module {

    ConcurrentLinkedQueue<ConcurrentSkipListSet<TempReading>> hourlyReadings = new ConcurrentLinkedQueue<>();

}
