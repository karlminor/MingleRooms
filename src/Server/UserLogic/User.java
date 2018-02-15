package Server.UserLogic;

import Server.MessageLogic.Mailbox;

import java.io.*;
import java.net.Socket;

public class User extends Thread {
    private boolean status;
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private Mailbox mailbox;

    private String name;
    private String avatar;

    private double x;
    private double y;

    private int currentRoom;

    public User(Socket socket, Mailbox mailbox){
        status = true;
        this.socket = socket;
        this.mailbox = mailbox;
        name = "User";
        currentRoom = 0;

    }

    public boolean status(){
        return status;
    }

    public void run(){
        System.out.println("User has joined with address: " +
                socket.getInetAddress());
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while(socket.isConnected()){
                handleConnection();
            }

        } catch (IOException|InterruptedException e) {
            System.out.println(name + " has left");
            status = false;
        }
    }

    public void handleConnection() throws InterruptedException, IOException {
        String message = input.readLine();
        String msg[];

        /* Finds the identifier of the message and takes different actions depending on this
        N = new user connected, format is name
        A = avatar information, format is avatar identifier
        P = position update, the string received will be format xx.xx:yy.yy should dx dy
        R = room update, the string received will be formatted as just the number for the room and the position split with : i.e. nn:xx.xx:yy.yy
        M = message, the string received will be the string message
        Q = quit
        */

        char identifier = message.charAt(0);
        message = message.substring(1);
        System.out.println(message);
        switch (identifier) {
            case ('N'):
                name = message;
                broadcast(name + " has joined the server");
                break;
            case ('A'):
                avatar = message;
                break;
            case ('P'):
                msg = message.split(":");
                updatePosition(Double.parseDouble(msg[0]), Double.parseDouble(msg[1]));
                break;
            case ('R'):
                msg = message.split(":");
                joinRoom(Integer.parseInt(msg[0]));
                setPosition(Double.parseDouble(msg[1]), Double.parseDouble(msg[2]));
                break;
            case ('M'):
                broadcast(message);
                break;
            case ('Q'):
                broadcast(name + " has left");
                disconnect();
                break;
            default:
                break;
        }
    }

    public void echo(String msg) throws IOException {
        output.write(msg + "\n");
        output.flush();
    }

    private void disconnect() throws IOException {
        status = false;
        input.close();
        output.close();
        socket.close();
    }

    private void broadcast(String msg) throws InterruptedException {
        System.out.println("Broadcast " + msg + " from " + name);
        mailbox.deposit(msg);
    }

    //This function is used only when a user joins a new room
    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }

    public String[] getInfo(){
        String[] info = {name, avatar};
        return info;
    }

    //Updates the location of the user
    public void updatePosition(double dx, double dy){
        x+=dx;
        y+=dy;
    }

    //This function sets allows us to know which room the user is in
    public void joinRoom(int room){
        currentRoom = room;
    }

    //Return true if the room number sent in is the same as the current room, else false
    public boolean sameRoom(int room){
        return currentRoom == room;
    }


}
