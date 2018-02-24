package Client;

import Client.gui.ChatRoomView;
import Client.gui.ClientGUI;
import javafx.application.Application;

public class ClientMain {

    public static void main(String[] args) {
        new ClientMain().run();
    }

    public void run(){
        Application.launch(ClientGUI.class);
    }

    private static ClientNetworkThread clientNetworkThread;

    // Is started from GUI if connection to server is successful
    public static void startNetworkThread(ChatRoomView chatRoomView, ClientGUI clientGUI) {
        clientNetworkThread = new ClientNetworkThread(chatRoomView, clientGUI);
        clientNetworkThread.start();
    }

    public static void stopNetworkThread() {
        clientNetworkThread.interrupt();
    }
}
