package Client.gui;

import Client.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class ChatRoomView extends HBox {
    // Size for this view only
    public static final int WIDTH = 900;
    public static final int HEIGHT = 500;

    private final int INSETS = 10;
    private final int SPACING = 10;

    private ClientGUI clientGUI;

    private GridPane board;
    private TextArea chat;
    private TextArea message;
    private final int BOARD_X_TILES = 5;
    private final int BOARD_Y_TILES = 5;

    public ChatRoomView(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
        containerSettings();
        initComponents();
    }

    private void containerSettings() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(INSETS, INSETS, INSETS, INSETS));
        setSpacing(SPACING);
        setOnKeyPressed(new KeyboardHandler());
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
        friendsOnlineLV.setOnKeyPressed(new KeyboardHandler());

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
        board = new GridPane();
        board.setMaxWidth(HEIGHT * 0.55);
        board.setMaxHeight(HEIGHT * 0.55);
        for(int y = 0; y < BOARD_Y_TILES; y++) {
            for(int x = 0; x < BOARD_X_TILES; x++) {
                TextArea temp = new TextArea(Integer.toString(x + y * BOARD_X_TILES + 1));
                temp.setEditable(false);
                temp.setId("boardTile");
                temp.setMouseTransparent(true);
                temp.setFocusTraversable(false);
                board.add(temp, x, y);
            }
        }

        // P2P and enter chat room buttons
        VBox innerSidePanel = new VBox();
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
        chatScroll.setOnKeyPressed(new KeyboardHandler());
        chatScroll.setFitToWidth(true);
        chat = new TextArea();
        chat.setOnKeyPressed(new KeyboardHandler());
        chat.setWrapText(true);
        chat.setPromptText("No chat messages received");
        chat.setPrefHeight(HEIGHT * 0.2);
        chat.setEditable(false);
        chatScroll.setContent(chat);

        // New message and send button
        HBox innerBottomPanel = new HBox();
        innerBottomPanel.setSpacing(SPACING);
        message = new TextArea();
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

    private void clearBoard() {
        for(int y = 0; y < BOARD_Y_TILES; y++) {
            for(int x = 0; x < BOARD_X_TILES; x++) {
                TextArea currentTile = (TextArea) board.getChildren().get(x + y * BOARD_X_TILES);
                currentTile.setBackground(Background.EMPTY);
                // TODO This also clears the base color...
                currentTile.getStyleClass().clear();
                currentTile.getStyleClass().addAll("text-input", "text-area");
            }
        }
    }

    private void drawIconInBoard(int x, int y, Image avatar) {
        if(x >= 0 && x < BOARD_X_TILES && y >= 0 && y < BOARD_Y_TILES) {
            TextArea currentTile = (TextArea) board.getChildren().get(x + y * BOARD_X_TILES);
            if(avatar != null) {
                // TODO Image is not scaled correctly
                BackgroundImage backgroundImage = new BackgroundImage(avatar, null, null, null, null);
                currentTile.setBackground(new Background(backgroundImage));
            }
            // TODO show player name also
        }
    }

    public void displayCharactersInGUI(ArrayList<User> users) {
        clearBoard();
        for(User u : users) {
            drawIconInBoard(u.getX(), u.getY(), u.getAvatar());
        }
    }

    private class KeyboardHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case UP:
                    System.out.println("UP");
                    break;
                case DOWN:
                    System.out.println("DOWN");
                    break;
                case LEFT:
                    System.out.println("LEFT");
                    break;
                case RIGHT:
                    System.out.println("RIGHT");
                    break;
            }
            event.consume();
        }
    }

    private class DisconnectButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // TODO
            if(clientGUI.getClientCommunication().disconnectFromServer()) {
                clientGUI.changeView(ClientGUI.FIRST_VIEW);
            } else {
                // TODO display error message in GUI
                System.out.println("Error disconnecting from server");
            }
        }
    }

    private class SendMessageButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            String text = message.getText();
            if(text != null && !text.isEmpty()) {
                if(clientGUI.getClientCommunication().sendMessage(text)) {
                    message.setText("");
                } else {
                    // TODO display error message in GUI
                    System.out.println("Error sending message to server");
                }
            }
        }
    }

}
