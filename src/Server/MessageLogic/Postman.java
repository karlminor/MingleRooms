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

                         /* Finds the identifier of the message and takes different actions depending on this
                            N = new user connected, format is name
                            A = avatar information, format is avatar identifier
                            P = position update, the string received will be format xxxx¤yyyy should dx dy
                            R = room update, the string received will be formatted as just the number for the room and the position split with : i.e. nn¤xx.xx¤yy.yy
                            M = message, the string received will be the string message
                            Q = quit
                        */

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