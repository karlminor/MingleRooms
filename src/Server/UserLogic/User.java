package Server.UserLogic;

import Server.MessageLogic.Mailbox;
import Server.MessageLogic.Message;

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
    private int id;

    private double x;
    private double y;

    private int currentRoom;

    private boolean settingUp;

    public User(Socket socket, Mailbox mailbox, int id){
        status = true;
        this.socket = socket;
        this.mailbox = mailbox;
        this.id = id;
        name = "User";
        currentRoom = 0;
        x = 0;
        y = 0;
        settingUp = false;
    }

    public boolean status(){
        return status;
    }

    public void setupComplete(){
        settingUp = true;
    }

    public void run(){
        System.out.println("User has joined with address: " +
                socket.getInetAddress());
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //settingUp is set to true by Users when the connection is established and all information needed has been shared
            while(!settingUp){
            }
            while(socket.isConnected()){
                handleConnection();
            }

        } catch (IOException|InterruptedException e) {
            System.out.println(name + " has left");
            status = false;
        }
    }

    //The first message from client must be format "name¤avatar" to set this up for the server. The server replies with the user's id as id
    public boolean setupConnection() throws IOException {
        String message = null;
        while(message==null) {
        	try {
    			message = input.readLine();
    		} catch (IOException|NullPointerException e) {}
        }
		
        if(message.matches(".*¤.*")){
            String msg[] = message.split("¤");
            name = msg[0];
            avatar = msg[1];
            echo(Integer.toString(id));
            return true;
        }
        return false;
    }

    private void handleConnection() throws InterruptedException, IOException {
        String message = input.readLine();
        String msg[];

        /* Finds the identifier of the message and takes different actions depending on this
        P = position update, the string received will be format Pxxxx¤yyyy should dx dy
        R = room update, the string received will be formatted as just the number for the room and the position split with : i.e. Rnn¤xxxx¤yyyy
        M = message, the string received will be the string message, format Mmessage
        Q = quit
        */

        char identifier = message.charAt(0);
        message = message.substring(1);
        System.out.println(message);
        switch (identifier) {
            case ('P'):
                msg = message.split("¤");
                updatePosition(Double.parseDouble(msg[0]), Double.parseDouble(msg[1]));
                mailbox.deposit(new Message(this, "P" + id + "¤" + x + "¤" + y));
                break;
            case ('R'):
                msg = message.split("¤");
                joinRoom(Integer.parseInt(msg[0]));
                setPosition(Double.parseDouble(msg[1]), Double.parseDouble(msg[2]));
                mailbox.deposit(new Message(this, "R" + currentRoom + "¤" + x + "¤" + y));
                break;
            case ('M'):
                mailbox.deposit(new Message(this, "M" + name + "¤" + message));
                break;
            case ('Q'):
                broadcast("S" + name + " has left the server");
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

    public void broadcast(String msg) throws InterruptedException {
        System.out.println("Broadcast " + msg + " from " + name);
        mailbox.deposit(new Message(this, msg));
    }

    //This function is used only when a user joins a new room
    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public String[] getInfo(){
        String[] info = {Integer.toString(id), name, avatar, Integer.toString(currentRoom), Double.toString(x), Double.toString(y)};
        return info;
    }

    //Updates the location of the user with format Pid¤xxxx¤yyyy
    public void updatePosition(double dx, double dy) throws InterruptedException {
        x+=dx;
        y+=dy;
    }

    //This function sets allows us to know which room the user is in format R¤room¤xxxx¤yyyy
    public void joinRoom(int room) throws InterruptedException {
        currentRoom = room;
    }

    //Return true if the user sent in is in the same as the current room, else false
    public boolean sameRoom(User u){
        return currentRoom == u.getRoom();
    }

    public int getRoom(){
        return currentRoom;
    }

    public int getUserId(){
        return id;
    }


}
