package Client;

import Client.gui.ClientGUI;
import javafx.application.Application;

public class ClientMain {

    public static void main(String[] args) {
        new ClientMain().run();
    }

    public void run(){
        Application.launch(ClientGUI.class);
    }
}
