package Server.MessageLogic;



import Server.UserLogic.User;
import Server.UserLogic.Users;

import java.io.IOException;

public class Postman extends Thread{
    Users users;
    Mailbox mailbox;

    public Postman(Users users, Mailbox mailbox){
        this.users = users;
        this.mailbox = mailbox;
    }

    public void run(){
        String message;
        while(true){
            try {
                message = mailbox.withdraw();
                for (User u : users.userList()){
                    if (u.status()) {
                        u.echo(message);
                    } else {
                        users.remove(u);
                        //Perform a clean up telling all users that the user disconnected etc
                        u.interrupt();
                    }
                }
            }
            catch (IOException|InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}