package Client.gui;

import Client.CommunicationCallsFromGUI;
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
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ChatRoomView extends HBox {
    // Size for this view only
    public static final int WIDTH = 900;
    public static final int HEIGHT = 500;

    private final int INSETS = 10;
    private final int SPACING = 10;

    private ClientGUI clientGUI;

    private GridPane board;
    private ArrayList<TextArea> boardTextAreas;
    private TextArea chat;
    private TextArea message;
    private ObservableList<String> friendsOnlineList;

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

        friendsOnlineList = FXCollections.observableArrayList();
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
        board.setPadding(new Insets(INSETS, INSETS, INSETS, INSETS));
        board.setGridLinesVisible(true);
        board.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255, 1), null, null)));
        board.setMaxWidth(HEIGHT * 0.55);
        board.setMaxHeight(HEIGHT * 0.55);
        boardTextAreas = new ArrayList<>();
        for(int y = 0; y < BOARD_Y_TILES; y++) {
            for(int x = 0; x < BOARD_X_TILES; x++) {
                TextArea temp = new TextArea(Integer.toString(x + y * BOARD_X_TILES + 1));
                temp.setEditable(false);
                temp.setId("boardTile");
                temp.setMouseTransparent(true);
                temp.setFocusTraversable(false);
                board.add(temp, x, y);
                boardTextAreas.add(temp);
            }
        }

        // P2P and enter chat room buttons
        VBox innerSidePanel = new VBox();
        innerSidePanel.setSpacing(SPACING);
        innerSidePanel.setAlignment(Pos.BOTTOM_RIGHT);
        Button p2pChat = new Button("Start P2P chat");
        p2pChat.setOnAction(new P2PButtonHandler());
        p2pChat.setPrefWidth(WIDTH * 0.15);
        p2pChat.setPrefHeight(HEIGHT * 0.06);
        Button enterChatRoom = new Button("Enter chat room: x");
        enterChatRoom.setOnAction(new EnterChatRoomHandler());
        enterChatRoom.setPrefHeight(p2pChat.getPrefHeight());
        enterChatRoom.setPrefWidth(p2pChat.getPrefWidth());
        innerSidePanel.getChildren().addAll(p2pChat, enterChatRoom);

        Region region1 = new Region();
        region1.setPrefWidth(WIDTH * 0.15);

        innerCenterPanel.setLeft(region1);
        innerCenterPanel.setCenter(board);
        innerCenterPanel.setRight(innerSidePanel);


        // Chat messages
        chat = new TextArea();
        chat.setOnKeyPressed(new KeyboardHandler());
        chat.setWrapText(true);
        chat.setPromptText("No chat messages received");
        chat.setPrefHeight(HEIGHT * 0.2);
        chat.setEditable(false);

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


        mainPanel.getChildren().addAll(innerTopPanel, innerCenterPanel, chat, innerBottomPanel);

        return mainPanel;
    }

    private void clearBoard() {
        for (int y = 0; y < BOARD_Y_TILES; y++) {
            for (int x = 0; x < BOARD_X_TILES; x++) {
                TextArea currentTile = (TextArea) boardTextAreas.get(x + y * BOARD_X_TILES);
                currentTile.setBackground(new Background(currentTile.getBackground().getFills(), null));
                currentTile.setText(Integer.toString(x + y * BOARD_X_TILES + 1));
            }
        }
    }

    private void drawIconInBoard(int x, int y, Image avatar, String nickname) {
        if(x >= 0 && x < BOARD_X_TILES && y >= 0 && y < BOARD_Y_TILES) {
            TextArea currentTile = (TextArea) boardTextAreas.get(x + y * BOARD_X_TILES);
            if(avatar != null) {
                // TODO Image is not scaled correctly
                BackgroundImage backgroundImage = new BackgroundImage(avatar, null, null, null, null);
                ArrayList<BackgroundImage> backgroundImages = new ArrayList<>();
                backgroundImages.add(backgroundImage);
                currentTile.setBackground(new Background(currentTile.getBackground().getFills(), backgroundImages));
            }
            currentTile.setText(nickname);
        }
    }

    public void displayCharactersInGUI(ArrayList<User> users) {
        clearBoard();
        // TODO check if users are in this chat room
        for(User u : users) {
            drawIconInBoard(u.getX(), u.getY(), u.getAvatar(), u.getNickname());
        }
    }

    public void updateFriendsOnline(ArrayList<User> users) {
        // TODO check if users are in this chat room
        friendsOnlineList.clear();
        for(User u : users) {
            friendsOnlineList.add(u.getNickname());
        }
    }

    public void updateChat(ArrayList<String> messages) {
        chat.clear();
        for(String s : messages) {
            chat.setText(chat.getText() + s + "\n");
        }
        chat.setScrollTop(Double.MAX_VALUE);
    }

    private class KeyboardHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            // TODO Maybe add some delay so that user can not walk too fast?
            switch (event.getCode()) {
                case UP:
                    System.out.println("UP");
                    clientGUI.getCommunicationCallsFromGUI().move(CommunicationCallsFromGUI.UP);
                    break;
                case DOWN:
                    System.out.println("DOWN");
                    clientGUI.getCommunicationCallsFromGUI().move(CommunicationCallsFromGUI.DOWN);
                    break;
                case LEFT:
                    System.out.println("LEFT");
                    clientGUI.getCommunicationCallsFromGUI().move(CommunicationCallsFromGUI.LEFT);
                    break;
                case RIGHT:
                    System.out.println("RIGHT");
                    clientGUI.getCommunicationCallsFromGUI().move(CommunicationCallsFromGUI.RIGHT);
                    break;
            }
            event.consume();
        }
    }

    private class DisconnectButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if(!clientGUI.getCommunicationCallsFromGUI().disconnectFromServer()) {
                clientGUI.showPopup(Alert.AlertType.WARNING, "Issues with connection", "Failed to disconnect from server", "...");
            }
            clientGUI.changeView(ClientGUI.FIRST_VIEW);
        }
    }

    private class SendMessageButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            String text = message.getText();
            if(text != null && !text.isEmpty()) {
                if(clientGUI.getCommunicationCallsFromGUI().sendMessage(text)) {
                    message.setText("");
                } else {
                    clientGUI.showPopup(Alert.AlertType.WARNING, "Issues with connection", "Failed to send message to server", "...");
                }
            }
        }
    }

    private class P2PButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // TODO
            clientGUI.showPopup(Alert.AlertType.INFORMATION, "Not yet implemented", "Not yet implemented", "...");
        }
    }

    private class EnterChatRoomHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // TODO
            clientGUI.showPopup(Alert.AlertType.INFORMATION, "Not yet implemented", "Not yet implemented", "...");
        }
    }

}
