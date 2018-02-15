package Server.MessageLogic;

import Server.UserLogic.User;

public class Mailbox {
    private Message message;
    private boolean full;

    public Mailbox(){
        full = false;
    }

    public synchronized void deposit(Message message) throws InterruptedException {
        while(full){
            wait();
        }
        full = true;
        this.message = message;
        notifyAll();

    }

    public synchronized Message withdraw() throws InterruptedException {
        while(!full){
            wait();
        }
        full = false;
        notifyAll();
        return message;
    }


}
