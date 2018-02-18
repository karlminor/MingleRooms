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
            socket = ss.accept();

            u = new User(socket, mailbox, count);
            u.start();
            users.add(u);
            count++;
        }
    }
}
