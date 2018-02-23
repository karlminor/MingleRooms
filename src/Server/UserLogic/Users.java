package Server.UserLogic;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

public class Users {
    private List<User> users;

    public Users(){
        users = new ArrayList<>();
    }

    public synchronized void add(User u) throws IOException {
        setupConnection(u);
        users.add(u);
    }

    public synchronized void remove(User u){
       users.remove(u);

    }

    public synchronized int size(){
        return users.size();
    }

    public synchronized List<User> userList(){
        return users;
    }
    //Sets up the connection which receives the name and avatar of the user and adds it to the user object,
    //send the number of users on the server currently as nbr
    //send all information about current users on the server to the user in format N¤id¤name¤avatar¤room¤xxxx¤yyyy
    //Sends N¤id¤name¤avatar¤room¤xxxx¤yyyy for the new user to all users except the new user itself
    public void setupConnection(User u) throws IOException {
        if (u.setupConnection()){
            String[] info;
            info = u.getInfo();
            u.echo("N¤" + info[0] + "¤" + info[1] + "¤" + info[2] + "¤" + info[3] + "¤" + info[4] + "¤" + info[5]);
            u.echo(Integer.toString(users.size()));
            for(User user : users){
                info = user.getInfo();
                u.echo("N¤" + info[0] + "¤" + info[1] + "¤" + info[2] + "¤" + info[3] + "¤" + info[4] + "¤" + info[5]);
                info = u.getInfo();
                user.echo("N¤" + info[0] + "¤" + info[1] + "¤" + info[2] + "¤" + info[3] + "¤" + info[4] + "¤" + info[5]);
            }
            u.setupComplete();
        }

    }
}
