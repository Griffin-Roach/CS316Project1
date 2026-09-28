import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CallCenter {
    public final static int totalCustomers = 20;
    public final static int totalAgents = 2;
    private final static Queue<Integer> queue = new LinkedList<>();
    private final static ReentrantLock qLock = new ReentrantLock();
    private final static Semaphore mutex = new Semaphore(1);

    private  final static Semaphore numberOfCalls = new Semaphore(0);
    //private final static Condition queueNotEmpty = qLock.newCondition();

    public static void addCall(int customerID) throws InterruptedException {
        //qLock.lock();
        mutex.acquire();

        try {
            queue.add(customerID);
            //queueNotEmpty.signal();
            System.out.println("Customer " + customerID + "entered queue.");
        }finally {
            qLock.unlock();
        }
    }

    public static int takeCall() throws Exception {
        int customerID;
        numberOfCalls.acquire();

        //qLock.lock();
        mutex.acquire();

        try {
            customerID = queue.remove();
        }
        finally {
            //qLock.unlock();
            mutex.release();
        }

        return customerID;

        //try {
        //    while (queue.isEmpty()) {
        //        queueNotEmpty.await();
        //    }
        //    customerID = queue.remove();
        //}
        //finally {
        //    qLock.unlock();
        //}
        //return customerID;
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService agentPool = Executors.newFixedThreadPool(4);
        ExecutorService customerPool = Executors.newCachedThreadPool();

        for (int i = 1; i<=totalAgents; i++) {
            agentPool.submit( new Agent(i));
        }
        for (int i = 1; i <= totalCustomers; i++) {
            customerPool.submit(new Customer(i));
            Thread.sleep(ThreadLocalRandom.current().nextInt(10,100));
        }
        customerPool.shutdown();
    }
}