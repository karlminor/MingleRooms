package Client.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatRoomView extends HBox {
    // Size for this view only
    public static final int WIDTH = 900;
    public static final int HEIGHT = 500;

    private ClientGUI clientGUI;

    public ChatRoomView(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
        containerSettings();
        initComponents();
    }

    private void containerSettings() {

    }

    private void initComponents() {
        getChildren().addAll(initLeftSidePanel(), initMainPanel());
    }

    private BorderPane initLeftSidePanel() {
        BorderPane leftSidePanel = new BorderPane();
        leftSidePanel.setMinHeight(200);
        Label friendsLB = new Label("Friends");
        Button disconnectB = new Button("Disconnect");
        disconnectB.setAlignment(Pos.BOTTOM_CENTER);

        //leftSidePanel.top
        return leftSidePanel;
    }

    private VBox initMainPanel() {
        VBox mainPanel = new VBox();
        Label chatRoomLB = new Label("You are in chat room: ...");

        GridPane board = new GridPane();
        board.setMaxSize(300, 300);
        for(int y = 0; y < 5; y++) {
            for(int x = 0; x < 5; x++) {
                TextArea temp = new TextArea(Integer.toString(x + y * 5 + 1));
                temp.setEditable(false);
                board.add(temp, x, y);
            }
        }

        mainPanel.getChildren().addAll(chatRoomLB, board);

        return mainPanel;
    }

}
