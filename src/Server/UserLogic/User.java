package Server.UserLogic;

import java.io.*;
import java.net.Socket;

public class User extends Thread {
    private boolean status;
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private String name;

    public User(Socket socket){
        status = true;
        this.socket = socket;
        name = "User";
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

        } catch (IOException e) {
            System.out.println(name + " has left");
            status = false;
        }
    }

    public void handleConnection(){
    }


}
