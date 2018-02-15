package Client.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatRoomView extends HBox {
    private ClientGUI clientGUI;

    public ChatRoomView(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
        containerSettings();
        initComponents();
    }

    private void containerSettings() {

    }

    private void initComponents() {
        VBox leftSidePanel = new VBox();
        Label friendsLB = new Label("Friends");

        leftSidePanel.getChildren().addAll(friendsLB);


        VBox mainPanel = new VBox();
        Label chatRoomLB = new Label("You are in chat room: ...");

        mainPanel.getChildren().addAll(chatRoomLB);

    }

}
