package Server.ServerLogic;

import Server.UserLogic.User;
import Server.UserLogic.Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private Socket socket;
    private User u;
    private ServerSocket ss;
    private Users users;

    public Server(int port) throws IOException {
        ss = new ServerSocket(port);
        users = new Users();
    }

    public void start() throws IOException {
        while(true){
            socket = ss.accept();

            //Checks if any of the users in the user list are disconnected and clears them from the user list before
            //creating the user and adding it to the user list
            for(User u : users.userList()){
                if(!u.status()){
                    users.remove(u);
                    //Perform a clean up telling all users that the user disconnected etc
                    u.interrupt();
                }
            }
            u = new User(socket);
            users.add(u);
            u.start();
        }
    }
}
