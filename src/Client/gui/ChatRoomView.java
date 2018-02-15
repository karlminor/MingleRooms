package Client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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

        ObservableList<String> friendsOnlineList = FXCollections.observableArrayList(
                "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
        ListView<String> friendsOnlineLV = new ListView<>(friendsOnlineList);

        Button disconnectB = new Button("Disconnect");

        leftSidePanel.setTop(friendsLB);
        leftSidePanel.setCenter(friendsOnlineLV);
        leftSidePanel.setBottom(disconnectB);
        return leftSidePanel;
    }

    private VBox initMainPanel() {
        VBox mainPanel = new VBox();
        Label chatRoomLB = new Label("You are in chat room: ...");

        HBox innerTopPanel = new HBox();

        GridPane board = new GridPane();
        board.setMaxSize(300, 300);
        for(int y = 0; y < 5; y++) {
            for(int x = 0; x < 5; x++) {
                TextArea temp = new TextArea(Integer.toString(x + y * 5 + 1));
                temp.setEditable(false);
                board.add(temp, x, y);
            }
        }

        VBox innerSidePanel = new VBox();
        innerSidePanel.setAlignment(Pos.BOTTOM_CENTER);

        Button p2pChat = new Button("Start P2P chat");
        Button enterChatRoom = new Button("Enter chat room: x");

        innerSidePanel.getChildren().addAll(p2pChat, enterChatRoom);

        innerTopPanel.getChildren().addAll(board, innerSidePanel);



        ScrollPane chatScroll = new ScrollPane();
        chatScroll.setFitToWidth(true);
        TextArea chat = new TextArea("Chat messages...");
        chat.setEditable(false);
        chatScroll.setContent(chat);

        HBox innerBottomPanel = new HBox();

        TextArea message = new TextArea("New message");

        Button send = new Button("Send");

        innerBottomPanel.getChildren().addAll(message, send);

        mainPanel.getChildren().addAll(chatRoomLB, innerTopPanel, chatScroll, innerBottomPanel);

        return mainPanel;
    }

}
