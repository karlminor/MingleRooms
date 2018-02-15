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
        users.add(u);
        setupConnection(u);
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
    //Send the users id to itself in format I¤id
    //Sends N¤id¤name¤avatar¤room¤xxxx¤yyyy to all users except the new user itself
    public void setupConnection(User u) throws IOException {
        String[] info;
        u.echo("I¤" + u.getUserId());
        for(User user : users){
            if(user != u) {
                info = u.getInfo();
                u.echo("N¤" + info[0] + "¤" + info[1] + "¤" + info[2] + "¤" + info[3] + "¤" + info[4] + "¤" + info[5]);
            }
        }
    }
}
