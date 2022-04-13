package presents;

import java.rmi.Remote;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Servant implements Runnable {
    public final int presentCount;
    final AtomicInteger thankYouCards = new AtomicInteger(0);
    final ConcurrentLinkedPresentList presentList = new ConcurrentLinkedPresentList();
    final ConcurrentLinkedDeque<Integer> presentBag;
    final ConcurrentLinkedQueue<Integer> addedPresents = new ConcurrentLinkedQueue<>();

    public Servant(int presentCount, List<Integer> presentBag) {
        this.presentCount = presentCount;
        // Turn Present bag into a concurrent Dequeue
        this.presentBag = new ConcurrentLinkedDeque<>(presentBag);
    }

    @Override
    public void run() {
        // Alternate between adding gifts & Thank-you note
        int turn = 0;
        while (!presentBag.isEmpty() || !presentList.isEmpty()) {
            if (turn % 2 == 0) {
                if (!presentBag.isEmpty())
                    addGift();
            } else {
                if (!presentList.isEmpty())
                    writeThankYouNote();
            }

            turn = (turn % 2) + 1;
        }
    }

    void writeThankYouNote() {
        // int cur = addedPresents.poll();
        // if (!presentList.remove(cur)) {
        // System.out.println("ERROR");
        // }
        if (presentList.poll() != null)
            thankYouCards.incrementAndGet();
    }

    void addGift() {
        int cur = presentBag.poll();
        presentList.add(cur);
        // addedPresents.add(cur);

    }

}
