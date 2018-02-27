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
    private int count;

    public Server(int port) throws IOException {
        ss = new ServerSocket(port);
        users = new Users();
        mailbox = new Mailbox();
        postman = new Postman(users, mailbox);
        count = 0;
    }

    public void start() throws IOException {
        postman.start();
        while(true){

            //When a client connects first creates the user, then starts the thread, then adds it to the user list.
            //Upon being added to the user list the server tells the client everything it needs to know to set up the connection
            socket = ss.accept();
            u = new User(socket, mailbox, postman, count);
            u.start();
            users.add(u);
            count++;
        }
    }
}
