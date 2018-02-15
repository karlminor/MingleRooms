package Server.MessageLogic;

public class Mailbox {
    String message = "";

    public Mailbox(){
    }

    public synchronized void deposit(String message) throws InterruptedException {
        while(!this.message.isEmpty()){
            wait();
        }
        this.message = message;
        notifyAll();

    }

    public synchronized String withdraw() throws InterruptedException {
        while(this.message.isEmpty()){
            wait();
        }
        String temp = message;
        message = "";
        notifyAll();
        return temp;
    }
}
