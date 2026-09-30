import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CallCenter {
    public final static int totalCustomers = 30;
    public final static int totalAgents = 3;
    final static Queue<Integer> arrivalQueue = new LinkedList<>();
    final static Queue<Integer> serviceQueue = new LinkedList<>();
    final static ReentrantLock qLock = new ReentrantLock();
    private final static Condition queueNotEmpty = qLock.newCondition();

    public static void addArrival(int customerID) throws InterruptedException {
        qLock.lock();

        try {
            arrivalQueue.add(customerID);
            queueNotEmpty.signal();
            System.out.println("Customer " + customerID + " entered arrival queue.");
        }finally {
            qLock.unlock();
        }
    }

    public static void addService(int customerID) throws InterruptedException {
        qLock.lock();
        try {
            int position = serviceQueue.size() + 1;
            serviceQueue.add(customerID);
            queueNotEmpty.signal();
            System.out.println("Customer " + customerID +
                    " entered service queue in position " + position);
        } finally {
            qLock.unlock();
        }
    }

    public static int takeCall() throws Exception {
        int customerID;

        qLock.lock();

        try {
            while (serviceQueue.isEmpty()) {
                queueNotEmpty.await();
            }
            customerID = serviceQueue.remove();
        }
        finally {
            qLock.unlock();
        }

        return customerID;
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService agentPool   = Executors.newFixedThreadPool(totalAgents);
        ExecutorService customerPool = Executors.newCachedThreadPool();
        ExecutorService greeterRun   = Executors.newSingleThreadExecutor();

        greeterRun.submit(new Greeter());
        for (int i = 1; i <= totalAgents; i++) {
            agentPool.submit(new Agent(i));
        }

        for (int i = 1; i <= totalCustomers; i++) {
            customerPool.submit(new Customer(i));
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
        }

        agentPool.shutdown();
        customerPool.shutdown();
        greeterRun.shutdown();
    }

}