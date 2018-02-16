package Client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class ChatRoomView extends HBox {
    // Size for this view only
    public static final int WIDTH = 900;
    public static final int HEIGHT = 500;

    private final int INSETS = 10;
    private final int SPACING = 10;

    private ClientGUI clientGUI;

    public ChatRoomView(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
        containerSettings();
        initComponents();
    }

    private void containerSettings() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(INSETS, INSETS, INSETS, INSETS));
        setSpacing(SPACING);
    }

    private void initComponents() {
        getChildren().addAll(initLeftSidePanel(), initMainPanel());
    }

    private BorderPane initLeftSidePanel() {
        BorderPane leftSidePanel = new BorderPane();
        leftSidePanel.setPrefWidth(WIDTH * 0.2);

        HBox innerTopPanel = new HBox();
        innerTopPanel.setAlignment(Pos.CENTER);
        Label friendsLB = new Label("Friends");
        innerTopPanel.getChildren().add(friendsLB);

        ObservableList<String> friendsOnlineList = FXCollections.observableArrayList(
                "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
        ListView<String> friendsOnlineLV = new ListView<>(friendsOnlineList);

        Button disconnectB = new Button("Disconnect");
        disconnectB.setOnAction(new DisconnectButtonHandler());
        disconnectB.setPrefHeight(HEIGHT * 0.06);
        disconnectB.setPrefWidth(leftSidePanel.getPrefWidth());

        leftSidePanel.setTop(innerTopPanel);
        leftSidePanel.setCenter(friendsOnlineLV);
        leftSidePanel.setBottom(disconnectB);

        BorderPane.setMargin(innerTopPanel, new Insets(0, 0, INSETS, 0));
        BorderPane.setMargin(friendsOnlineLV, new Insets(0, 0, INSETS, 0));
        BorderPane.setMargin(disconnectB, new Insets(0, 0, 0, 0));
        return leftSidePanel;
    }

    private VBox initMainPanel() {
        VBox mainPanel = new VBox();
        mainPanel.setSpacing(SPACING);

        mainPanel.setPrefWidth(WIDTH * 0.8);

        // Chat room title
        HBox innerTopPanel = new HBox();
        innerTopPanel.setAlignment(Pos.CENTER);
        Label chatRoomLB = new Label("You are in chat room: ...");
        innerTopPanel.getChildren().add(chatRoomLB);

        // Board
        BorderPane innerCenterPanel = new BorderPane();
        //innerCenterPanel.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        GridPane board = new GridPane();
        //board.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        board.setMaxWidth(HEIGHT * 0.55);
        board.setMaxHeight(HEIGHT * 0.55);
        for(int y = 0; y < 5; y++) {
            for(int x = 0; x < 5; x++) {
                TextArea temp = new TextArea(Integer.toString(x + y * 5 + 1));
                temp.setEditable(false);
                //temp.setBackground(Background.EMPTY);
                //temp.setBackground(new Background(new BackgroundImage(new Image("test.jpg"), null, null, null, null)));
                //temp.set
                board.add(temp, x, y);
            }
        }

        // P2P and enter chat room buttons
        VBox innerSidePanel = new VBox();
        //innerSidePanel.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
        innerSidePanel.setSpacing(SPACING);
        innerSidePanel.setAlignment(Pos.BOTTOM_RIGHT);
        Button p2pChat = new Button("Start P2P chat");
        p2pChat.setPrefWidth(WIDTH * 0.15);
        p2pChat.setPrefHeight(HEIGHT * 0.06);
        Button enterChatRoom = new Button("Enter chat room: x");
        enterChatRoom.setPrefHeight(p2pChat.getPrefHeight());
        enterChatRoom.setPrefWidth(p2pChat.getPrefWidth());
        innerSidePanel.getChildren().addAll(p2pChat, enterChatRoom);

        Region region1 = new Region();
        region1.setPrefWidth(WIDTH * 0.15);

        innerCenterPanel.setLeft(region1);
        innerCenterPanel.setCenter(board);
        innerCenterPanel.setRight(innerSidePanel);


        // Chat messages
        ScrollPane chatScroll = new ScrollPane();
        chatScroll.setFitToWidth(true);
        TextArea chat = new TextArea();
        chat.setWrapText(true);
        chat.setPromptText("No chat messages received");
        chat.setPrefHeight(HEIGHT * 0.2);
        chat.setEditable(false);
        chatScroll.setContent(chat);

        // New message and send button
        HBox innerBottomPanel = new HBox();
        innerBottomPanel.setSpacing(SPACING);
        TextArea message = new TextArea();
        message.setWrapText(true);
        message.setPromptText("Enter a chat message");
        message.setPrefHeight(HEIGHT * 0.15);
        message.setPrefWidth(WIDTH * 0.65);
        Button send = new Button("Send");
        send.setOnAction(new SendMessageButtonHandler());
        send.setPrefWidth(WIDTH * 0.15);
        send.setPrefHeight(message.getPrefHeight());
        innerBottomPanel.getChildren().addAll(message, send);


        mainPanel.getChildren().addAll(innerTopPanel, innerCenterPanel, chatScroll, innerBottomPanel);

        return mainPanel;
    }

    private void drawIconInBoard(int x, int y) {

    }

    private class DisconnectButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // TODO

            clientGUI.changeView(ClientGUI.FIRST_VIEW);
        }
    }

    private class SendMessageButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // TODO
        }
    }

}
