import java.util.concurrent.ThreadLocalRandom;

public class Agent implements Runnable {
    int ID;

    public Agent(int id) {
        this.ID = id;
    }

    public void run() {
        int customerPerAgent = CallCenter.totalCustomers/CallCenter.totalAgents;
        for (int i = 0; i < customerPerAgent; i++) {
            try {
                int customerID = CallCenter.takeCall();
                Thread.sleep(ThreadLocalRandom.current().nextInt(50,500));
                System.out.println("Agent " + ID + " finished serving customer " + customerID);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
