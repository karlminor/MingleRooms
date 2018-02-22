package Client.gui;

import Client.CommunicationCallsFromGUI;
import Client.P2PConnection;
import Client.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

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
    private TextArea chatRoomJoinLeaveHistory;
    private Button send;

    private boolean createAvatarImages = true;
    private HashMap<String, Image> avatarImages;

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

        VBox innerCenterPanel = new VBox();
        innerCenterPanel.setSpacing(SPACING);
        friendsOnlineList = FXCollections.observableArrayList();
        ListView<String> friendsOnlineLV = new ListView<>(friendsOnlineList);
        friendsOnlineLV.setOnKeyPressed(new KeyboardHandler());

        TextArea chatRoomJoinLeaveHistory = new TextArea();
        chatRoomJoinLeaveHistory.setEditable(false);
        chatRoomJoinLeaveHistory.setPrefHeight(HEIGHT * 0.51);
        chatRoomJoinLeaveHistory.setOnKeyPressed(new KeyboardHandler());
        chatRoomJoinLeaveHistory.setPromptText("Chat room users join/leave history");

        innerCenterPanel.getChildren().addAll(friendsOnlineLV, chatRoomJoinLeaveHistory);

        Button disconnectB = new Button("Disconnect");
        disconnectB.setOnAction(new DisconnectButtonHandler());
        disconnectB.setPrefHeight(HEIGHT * 0.06);
        disconnectB.setPrefWidth(leftSidePanel.getPrefWidth());

        leftSidePanel.setTop(innerTopPanel);
        leftSidePanel.setCenter(innerCenterPanel);
        leftSidePanel.setBottom(disconnectB);

        BorderPane.setMargin(innerTopPanel, new Insets(0, 0, INSETS, 0));
        BorderPane.setMargin(innerCenterPanel, new Insets(0, 0, INSETS, 0));
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
        board.setMinWidth(HEIGHT * 0.55);
        board.setMinHeight(HEIGHT * 0.55);
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
        message.setOnKeyPressed(new MessageKeyboardHandler());
        message.setWrapText(true);
        message.setPromptText("Enter a chat message");
        message.setPrefHeight(HEIGHT * 0.15);
        message.setPrefWidth(WIDTH * 0.65);
        send = new Button("Send");
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
                if(currentTile != null) {
                    Background currentBG = currentTile.getBackground();
                    Background temp = null;
                    if(currentBG != null) {
                        temp = new Background(currentBG.getFills(), null);
                    }
                    if(temp != null) {
                        currentTile.setBackground(temp);
                    }
                    currentTile.setText(Integer.toString(x + y * BOARD_X_TILES + 1));
                }

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

                if(currentTile.getBackground() != null) {
                    currentTile.setBackground(new Background(currentTile.getBackground().getFills(), backgroundImages));
                } else {
                    currentTile.setBackground(new Background(backgroundImage));
                }
            }
            currentTile.setText(nickname);
        }
    }

    public void displayCharactersInGUI(ArrayList<User> users) {
        if(createAvatarImages) {
            createAvatarImages();
            createAvatarImages = false;
        }
        clearBoard();
        // TODO check if users are in this chat room
        for(User u : users) {
            Image avatar = avatarImages.get(u.getAvatarName());
            if(avatar != null) {
                drawIconInBoard(u.getX(), u.getY(), avatar, u.getNickname());
            } else {
                System.out.println("Problem drawing avatar...");
            }
        }
    }

    private void createAvatarImages() {
        ArrayList<String> avatars = FirstView.getAvailableAvatars();
        avatarImages = new HashMap<>();
        double width = boardTextAreas.get(0).getWidth();
        double height = boardTextAreas.get(0).getHeight();
        for(String fileName : avatars) {
            try {
                Image avatarImage = new Image(FirstView.AVATAR_FOLDER_PATH_FOR_JAVAFX + fileName, width, height, false, true);
                avatarImages.put(fileName, avatarImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void updateChatRoomJoinLeaveHistory(ArrayList<String> history) {
        chatRoomJoinLeaveHistory.clear();
        for(String s : history) {
            chatRoomJoinLeaveHistory.setText(chatRoomJoinLeaveHistory.getText() + s + "\n");
        }
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
                case ENTER:
                    System.out.println("ENTER");
                    send.fire();
                    break;
            }
            event.consume();
        }
    }

    private class MessageKeyboardHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case ENTER:
                    send.fire();
                    event.consume();
                    break;
            }
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
                text = text.replaceAll("¤", "");
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
            Stage stage = new Stage();
            stage.setTitle("Peer-to-peer chat");


            String otherUserName = "TEMP";
            // TODO Get the user that we want to communicate with from the friends list and then get their id
            int idOtherUser = 0;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm P2P");
            alert.setHeaderText("Do you wish to start a p2p-connection with " + otherUserName + "?");
            alert.setContentText("If you still want to start a p2p-connection, but not with " + otherUserName + ", then select the user you wish to communicate with from the friend list.");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent()) {
                if(!result.get().getButtonData().isCancelButton()) {
                    P2PConnection p2pConnection = clientGUI.getCommunicationCallsFromGUI().startP2PChat(idOtherUser);
                    if(p2pConnection !=null) {
                        // TODO Show P2P view

                    }
                    P2PChatView root = new P2PChatView(clientGUI.getCommunicationCallsFromGUI(), p2pConnection, stage);
                    Scene scene = new Scene(root, P2PChatView.WIDTH, P2PChatView.HEIGHT);
                    stage.setScene(scene);
                    stage.show();
                }

            }
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
