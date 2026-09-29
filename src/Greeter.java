import java.util.concurrent.ThreadLocalRandom;

public class Greeter implements Runnable {

    public void run() {
        CallCenter.qLock.lock();
        for (Integer ID: CallCenter.arrivalQueue) {
            try {
                try {
                    CallCenter.addService(ID);
                    Thread.sleep(ThreadLocalRandom.current().nextInt(20,200));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                CallCenter.arrivalQueue.remove(ID);
            } finally {
                CallCenter.qLock.unlock();
            }
        }
    }
}
