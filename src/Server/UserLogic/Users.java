package Server.UserLogic;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

public class Users {
    private List<User> users;

    public Users(){
        users = new ArrayList<>();
    }

    public synchronized void add(User u) {
        setupConnection(u);
    }

    public synchronized void remove(User u){
       users.remove(u);

    }


    public synchronized List<User> userList(){
        return users;
    }
    //Sets up the connection which receives the name and avatar of the user and adds it to the user object,
    //send the number of users on the server currently as nbr
    //send all information about current users on the server to the user in format N¤id¤name¤avatar¤room¤xxxx¤yyyy
    //Sends N¤id¤name¤avatar¤room¤xxxx¤yyyy for the new user to all users except the new user itself
    public void setupConnection(User u) {
        try {
            if (u.setupConnection()) {
                System.out.println("Börjar skicka användarinfo");
                String[] info;
                info = u.getInfo();
                u.echo("N¤" + info[0] + "¤" + info[1] + "¤" + info[2] + "¤" + info[3] + "¤" + info[4] + "¤" + info[5]);
                u.echo(Integer.toString(users.size()));
                if (!users.isEmpty()) {
                    for (User user : users) {
                        try {
                            info = user.getInfo();
                            u.echo("N¤" + info[0] + "¤" + info[1] + "¤" + info[2] + "¤" + info[3] + "¤" + info[4] + "¤" + info[5]);
                            info = u.getInfo();
                            user.echo("N¤" + info[0] + "¤" + info[1] + "¤" + info[2] + "¤" + info[3] + "¤" + info[4] + "¤" + info[5]);
                        } catch (IOException e) {
                            user.disconnect();
                        }
                    }
                }
                System.out.println("Client setup complete");
            }

        } catch (IOException|NullPointerException e){
            System.out.println("User set up failed");
            u.interrupt();
        }
        users.add(u);

    }
}
