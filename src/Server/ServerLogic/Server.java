package Server.ServerLogic;

import Server.MessageLogic.Mailbox;
import Server.MessageLogic.Postman;
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
    private Mailbox mailbox;
    private Postman postman;

    public Server(int port) throws IOException {
        ss = new ServerSocket(port);
        users = new Users();
        mailbox = new Mailbox();
        postman = new Postman(users, mailbox);
    }

    public void start() throws IOException {
        postman.start();
        while(true){
            socket = ss.accept();



            //Checks if any of the users in the user list are disconnected and clears them from the user list before
            //creating the user and adding it to the user list
            u = new User(socket, mailbox);
            users.add(u);
            u.start();
        }
    }
}
