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
        Message message;
        User sender;
        String text;
        int id;
        while(true){
            try {
                message = mailbox.withdraw();
                sender = message.sender;
                text = message.text;
                id = sender.getUserId();
                for (User u : users.userList()){
                    if (u.status()) {
                        char identifier = text.charAt(0);
                        switch (identifier) {
                            case ('P'):
                                if (sender.sameRoom(u.getRoom())){
                                    u.echo(text);
                                }
                                break;
                            case ('R'):

                                break;
                            case ('M'):

                                break;
                            case ('Q'):
                                
                                break;
                            default:
                                break;
                        }

                        u.echo(message.text);
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