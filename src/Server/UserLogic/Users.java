package Server.UserLogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Users {
    private List<User> users;

    public Users(){
        users = new ArrayList<>();
    }

    public synchronized void add(User u){
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
}
