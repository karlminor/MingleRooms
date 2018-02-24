package Client;

import Client.gui.ChatRoomView;
import Client.gui.ClientGUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

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
        Alert alert = showBlockingAlert();
        clientNetworkThread = new ClientNetworkThread(chatRoomView, clientGUI, alert);
        clientNetworkThread.start();
    }

    private static Alert showBlockingAlert() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setContentText("Waiting for network, please wait...");
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.getDialogPane().getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
            }
        });
        alert.show();
        return alert;
    }

    public static void stopNetworkThread() {
        clientNetworkThread.interrupt();
    }
}
