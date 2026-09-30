import java.util.concurrent.ThreadLocalRandom;

public class Greeter implements Runnable {

    public void run() {
        int greeted = 0;

        while (greeted < CallCenter.totalCustomers) {
            Integer id = null;

            CallCenter.qLock.lock();
            try {
                if (!CallCenter.arrivalQueue.isEmpty()) {
                    id = CallCenter.arrivalQueue.remove();
                }
            } finally {
                CallCenter.qLock.unlock();
            }

            if (id != null) {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(20, 200)); // greeting
                    CallCenter.addService(id); // moves to service queue, signals agents
                    greeted++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
