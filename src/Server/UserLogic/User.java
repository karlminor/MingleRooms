package Server.UserLogic;

import Server.MessageLogic.Mailbox;
import Server.MessageLogic.Message;
import Server.MessageLogic.Postman;

import java.io.*;
import java.net.Socket;

public class User extends Thread {
    private boolean status;
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private Mailbox mailbox;
    private Postman postman;

    private String name;
    private String avatar;
    private int id;

    private int x;
    private int y;

    private int currentRoom;


    public User(Socket socket, Mailbox mailbox, Postman postman, int id) {
        status = true;
        this.socket = socket;
        this.mailbox = mailbox;
        this.postman = postman;
        this.id = id;
        name = "User";
        currentRoom = 0;
        x = 0;
        y = 0;

    }

    public boolean status() {
        return status;
    }


    public void run() {
        System.out.println("User has joined with socket address: " +
                socket.getRemoteSocketAddress().toString());
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //settingUp is set to true by Users when the connection is established and all information needed has been shared
            while (true) {

                handleConnection();
            }


        } catch (IOException | InterruptedException | NullPointerException e) {
            System.out.println(name + " has left");
            status = false;
        }
    }

    //The first message from client must be format "name¤avatar" to set this up for the server. The server replies with the user's id as id
    public boolean setupConnection() throws IOException {
        String message = null;
        while (message == null) {
            try {
                message = input.readLine();
                System.out.println(message);
            } catch (NullPointerException e) {
            }
        }

        if (message.matches(".*¤.*")) {
            String msg[] = message.split("¤");
            name = msg[0];
            avatar = msg[1];
            return true;
        }
        return false;
    }

    private void handleConnection() throws InterruptedException, IOException, NullPointerException {
        String message = input.readLine();
        System.out.println(message);
        String msg[];

        /* Finds the identifier of the message and takes different actions depending on this
        P = position update, the string received will be format Pxxxx¤yyyy should dx dy
        R = room update, the string received will be formatted as just the number for the room and the position split with : i.e. Rnn¤xxxx¤yyyy
        M = message, the string received will be the string message, format Mmessage
        Q = quit
        C = Peer to peer connection request formatted as C¤id with id being the other clients id which will result in a message sent to the receiving id
        */
        try {
            char identifier = message.charAt(0);
            message = message.substring(1);
            switch (identifier) {
                case ('P'):
                    msg = message.split("¤");
                    updatePosition(Integer.parseInt(msg[0]), Integer.parseInt(msg[1]));
                    mailbox.deposit(new Message(this, "P" + id + "¤" + x + "¤" + y));
                    break;
                case ('R'):
                    msg = message.split("¤");
                    joinRoom(Integer.parseInt(msg[0]));
                    setPosition(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]));
                    mailbox.deposit(new Message(this, "R" + currentRoom + "¤" + x + "¤" + y));
                    break;
                case ('M'):
                    mailbox.deposit(new Message(this, "M" + message));
                    break;
                case ('Q'):
                    disconnect();
                    break;
                case ('C'):
                    mailbox.deposit(new Message(this, "C" + message));
                    break;
                default:
                    break;
            }
        } catch (StringIndexOutOfBoundsException e) {

        }

    }

    public void echo(String msg) throws IOException, NullPointerException {
        System.out.println("Skriver " + msg + " till " + name);
        output.write(msg + "\n");
        output.flush();
    }

    public void disconnect() throws IOException {
        status = false;
        input.close();
        output.close();
        socket.close();
        postman.notifyExitAll(this);
    }


    //This function is used only when a user joins a new room
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String[] getInfo() {
        String[] info = {Integer.toString(id), name, avatar, Integer.toString(currentRoom), Integer.toString(x), Integer.toString(y)};
        return info;
    }

    //Updates the location of the user with format Pid¤xxxx¤yyyy
    public void updatePosition(double dx, double dy) throws InterruptedException {
        x += dx;
        y += dy;
    }

    //This function sets allows us to know which room the user is in format R¤room¤xxxx¤yyyy
    public void joinRoom(int room) throws InterruptedException {
        currentRoom = room;
    }

    //Return true if the user sent in is in the same as the current room, else false
    public boolean sameRoom(User u) {
        return currentRoom == u.getRoom();
    }

    public int getRoom() {
        return currentRoom;
    }
    
    public String getLocalAddress() {
    	return socket.getLocalAddress().toString();
    }

}
