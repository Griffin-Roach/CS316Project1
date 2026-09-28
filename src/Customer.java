public class Customer implements Runnable{
    int ID;

    public Customer(int id) {
        this.ID = id;
    }

    public void run() {
        try {
            CallCenter.addCall(ID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
