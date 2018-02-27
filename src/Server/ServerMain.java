package Server;

import Server.ServerLogic.Server;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args){
        new ServerMain().run();
    }

    public void run(){
        try {
            Server server = new Server(30000);
            System.out.println("Starting server with port: 30000");
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
