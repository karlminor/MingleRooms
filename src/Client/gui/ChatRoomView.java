package Client.gui;

import Client.ClientMain;
import Client.CommunicationCallsFromGUI;
import Client.P2PConnectionImpl;
import Client.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

    private volatile User client;

    private ListView<String> friendsOnlineLV;
    private ObservableList<String> friendsOnlineList;
    private TextArea chatRoomJoinLeaveHistory;
    private Label chatRoomLB;
    private GridPane board;
    private ArrayList<TextArea> boardTextAreas;
    private TextArea chat;
    private TextArea message;
    private Button p2pChatB;
    private Button enterChatRoomB;
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
        FontHandler.setTitleFont(friendsLB, 1.3);
        innerTopPanel.getChildren().add(friendsLB);

        VBox innerCenterPanel = new VBox();
        innerCenterPanel.setSpacing(SPACING);
        friendsOnlineList = FXCollections.observableArrayList();
        friendsOnlineLV = new ListView<>(friendsOnlineList);
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
        chatRoomLB = new Label("You are in chat room: ...");
        FontHandler.setTitleFont(chatRoomLB, 1.3);
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
                temp.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        event.consume();
                    }
                });
                temp.setEditable(false);
                temp.setWrapText(true);
                temp.setId("boardTile");
                temp.setTooltip(new Tooltip());
                //temp.setMouseTransparent(true); NOT NEEDED (Event filter above will consume event)
                temp.setFocusTraversable(false);
                temp.getTooltip().setFont(Font.font(temp.getTooltip().getFont().getSize()));
                temp.setFont(new Font(temp.getFont().getName(), temp.getFont().getSize() * 0.75));
                board.add(temp, x, y);
                boardTextAreas.add(temp);
            }
        }

        // P2P and enter chat room buttons
        VBox innerSidePanel = new VBox();
        innerSidePanel.setSpacing(SPACING);
        innerSidePanel.setAlignment(Pos.BOTTOM_RIGHT);
        p2pChatB = new Button("Start P2P chat");
        p2pChatB.setDisable(true);
        p2pChatB.setOnAction(new P2PButtonHandler());
        p2pChatB.setPrefWidth(WIDTH * 0.15);
        p2pChatB.setPrefHeight(HEIGHT * 0.06);
        enterChatRoomB = new Button("Enter chat room: x");
        enterChatRoomB.setOnAction(new EnterChatRoomHandler());
        enterChatRoomB.setPrefHeight(p2pChatB.getPrefHeight());
        enterChatRoomB.setPrefWidth(p2pChatB.getPrefWidth());
        innerSidePanel.getChildren().addAll(p2pChatB, enterChatRoomB);

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
                TextArea currentTile = boardTextAreas.get(x + y * BOARD_X_TILES);
                if(currentTile != null) {
                    if(currentTile.getBackground() != null) {
                        currentTile.setBackground(new Background(currentTile.getBackground().getFills(), null));
                    } else {
                        System.out.println("Background is null");
                    }
                    Tooltip currentTooltip = currentTile.getTooltip();
                    if(currentTooltip != null) {
                        currentTooltip.setText("");
                    }
                    if(client.getChatRoom() == 0) {
                        // Only set numbers on tiles if we are in the main room
                        currentTile.setText(Integer.toString(x + y * BOARD_X_TILES + 1));
                    } else {
                        currentTile.setText("");
                    }
                } else {
                    System.out.println("Current tile is null");
                }
            }
        }
    }

    private void displayRoomPopulation(ArrayList<User> users) {
        if(client.getChatRoom() == 0) {
            // TODO Do we need to use a map instead to save memory?
            int[] population = new int[BOARD_X_TILES * BOARD_Y_TILES];
            for(User u : users) {
                if(u.getChatRoom() != 0) {
                    population[u.getChatRoom() - 1]++;
                }
            }

            for(int i = 0; i < population.length; i++) {
                int p = population[i];
                //System.out.println("Population " + p + " in room " + (i + 1));
                if(p != 0) {
                    TextArea currentTile = boardTextAreas.get(i);
                    if(currentTile != null) {
                        String currentText = currentTile.getText();
                        if(!currentText.isEmpty()) {
                            currentTile.setText(currentText + "\n("+ p + " u)");
                        }
                        Tooltip currentTooltip = currentTile.getTooltip();
                        if(currentTooltip != null) {
                            //System.out.println("Tooltip not null in room " + (i + 1));
                            String currentTooltipText = currentTooltip.getText();
                            if(!currentTooltipText.isEmpty()) {
                                //System.out.println("Tooltip text not empty in room " + (i + 1));
                                currentTooltip.setText(currentTooltipText + "\n("+ p + " u)");
                            } else {
                                //System.out.println("Tooltip text empty in room " + (i + 1));
                                currentTooltip.setText("("+ p + " u)");
                            }
                        } else {
                            //System.out.println("Tooltip null in room " + (i + 1));
                        }
                    }
                }
            }
        }
    }

    private void drawIconInBoard(int x, int y, Image avatar, String nickname, int id) {
        if(x >= 0 && x < BOARD_X_TILES && y >= 0 && y < BOARD_Y_TILES) {
            TextArea currentTile = boardTextAreas.get(x + y * BOARD_X_TILES);
            if(avatar != null) {
                BackgroundImage backgroundImage = new BackgroundImage(avatar, null, null, null, null);
                ArrayList<BackgroundImage> backgroundImages = new ArrayList<>();
                backgroundImages.add(backgroundImage);

                if(currentTile.getBackground() != null) {
                    currentTile.setBackground(new Background(currentTile.getBackground().getFills(), backgroundImages));
                } else {
                    System.out.println("Background in draw is null... Creating new");
                    currentTile.setBackground(new Background(backgroundImage));
                }
            } else {
                System.out.println("Avatar is null");
            }

            Tooltip currentTooltip = currentTile.getTooltip();
            if(currentTooltip != null) {
                if(!currentTooltip.getText().isEmpty()) {
                    currentTooltip.setText(currentTooltip.getText() + ",\n" + nickname + " (" + id + ")");
                } else {
                    currentTooltip.setText(nickname + " (" + id + ")");
                }
            }
            currentTile.setText(nickname + " (" + id + ")");
        }
    }

    public void displayCharactersInGUI(ArrayList<User> sameRoomUsers, ArrayList<User> allUsers) {
        if(createAvatarImages) {
            createAvatarImages();
            createAvatarImages = false;
        }
        clearBoard();
        for(User u : sameRoomUsers) {
            if(u.getChatRoom() == client.getChatRoom()) {
                Image avatar = avatarImages.get(u.getAvatarName());
                if(avatar != null) {
                    drawIconInBoard(u.getX(), u.getY(), avatar, u.getNickname(), u.getId());
                } else {
                    System.out.println("Problem drawing avatar...");
                }
                if(u == client) {
                    if(u.getChatRoom() == 0) {
                        // Only update enter chat room button if the client is in the main chat room
                        updateEnterRoomButton();
                    }
                }
            }
        }
        displayRoomPopulation(allUsers);
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
        friendsOnlineList.clear();
        for(User u : users) {
            if(u != client && u.getChatRoom() == client.getChatRoom()) {
                friendsOnlineList.add(u.getNickname() + " (" + u.getId() + ")");
            }
        }
        if(friendsOnlineList.size() > 0) {
            p2pChatB.setDisable(false);
        } else {
            p2pChatB.setDisable(true);
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

    public void changeCurrentRoomTitle(int chatRoom) {
        if(chatRoom == 0) {
            chatRoomLB.setText("You are in chat room: Main Room");
        } else {
            chatRoomLB.setText("You are in chat room: " + chatRoom);
        }
    }

    private void updateEnterRoomButton() {
        int selectedChatRoom = client.getX() + 1 + client.getY() * BOARD_X_TILES;
        enterChatRoomB.setText("Enter chat room: " + selectedChatRoom);
    }

    public void setClient(User client) {
        this.client = client;
        changeCurrentRoomTitle(client.getChatRoom());
        updateEnterRoomButton();
    }
    
    public void startP2PConnection (P2PConnectionImpl p2pConnection) {
        if (p2pConnection != null) {
            Stage stage = new Stage();
            stage.setTitle("Peer-to-peer chat");
            P2PChatView root = new P2PChatView(p2pConnection, stage);
            Scene scene = new Scene(root, P2PChatView.WIDTH, P2PChatView.HEIGHT);
            stage.setScene(scene);
            stage.show();
            p2pConnection.setP2PChatView(root);
        } else {
            clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with connection", "No P2P-connection established", "...");
        }
    }

    private class KeyboardHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case UP:
                    System.out.println("UP");
                    if(client.getY() > 0) {
                        clientGUI.getCommunicationCallsFromGUI().move(CommunicationCallsFromGUI.UP);
                    }
                    break;
                case DOWN:
                    System.out.println("DOWN");
                    if(client.getY() < BOARD_Y_TILES - 1) {
                        clientGUI.getCommunicationCallsFromGUI().move(CommunicationCallsFromGUI.DOWN);
                    }
                    break;
                case LEFT:
                    System.out.println("LEFT");
                    if(client.getX() > 0) {
                        clientGUI.getCommunicationCallsFromGUI().move(CommunicationCallsFromGUI.LEFT);
                    }
                    break;
                case RIGHT:
                    System.out.println("RIGHT");
                    if(client.getX() < BOARD_X_TILES - 1) {
                        clientGUI.getCommunicationCallsFromGUI().move(CommunicationCallsFromGUI.RIGHT);
                    }
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
            ClientMain.stopNetworkThread();
            clientGUI.changeView(ClientGUI.FIRST_VIEW);
        }
    }

    private class SendMessageButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            String text = message.getText();
            if(text != null && !text.isEmpty()) {
                text = text.replaceAll("¤", "");
                if(!text.isEmpty()) {
                    if(clientGUI.getCommunicationCallsFromGUI().sendMessage(text)) {
                        message.setText("");
                    } else {
                        clientGUI.showPopup(Alert.AlertType.WARNING, "Issues with connection", "Failed to send message to server", "...");
                    }
                }
            }
        }
    }

    private class P2PButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            int selectedIndex = friendsOnlineLV.getSelectionModel().getSelectedIndex();
            if (selectedIndex > -1) {
                String selection = friendsOnlineList.get(selectedIndex);
                String otherUserName = selection.substring(0, selection.lastIndexOf('('));
                int otherUserID = Integer.parseInt(selection.substring(selection.lastIndexOf('(') + 1, selection.length() - 1));

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm P2P");
                alert.setHeaderText("Do you wish to start a p2p-connection with " + selection + "?");
                alert.setContentText("If you still want to start a p2p-connection, but not with " + selection + ", then select the user you wish to communicate with from the friend list.");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent()) {
                    if (!result.get().getButtonData().isCancelButton()) {
                        if(!clientGUI.getCommunicationCallsFromGUI().startP2PChat(otherUserID)) {
                        	clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with connection", "No P2P-connection established", "...");
                        }
                    }
                }
            } else {
                // User must select a user to communicate with
                clientGUI.showPopup(Alert.AlertType.WARNING, "Selection missing", "Please select the user that you wish to start a P2P-connection with.", "Selections are made in the friends list to your left.");
            }
        }
    }

    private class EnterChatRoomHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if(client.getChatRoom() == 0) {
                // Client is inside main room -> Disconnect from server
                int selectedChatRoom = client.getX() + 1 + client.getY() * BOARD_X_TILES;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm chat room change");
                alert.setHeaderText("Do you wish to enter chat room " + selectedChatRoom + "?");
                Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent()) {
                    if(!result.get().getButtonData().isCancelButton()) {
                        boolean success = clientGUI.getCommunicationCallsFromGUI().enterChatRoom(selectedChatRoom);
                        clearBoard(); // TODO Is there a risk that this will hide the character?
                        if(success) {
                            enterChatRoomB.setText("Leave chat room: " + selectedChatRoom);
                        } else {
                            clientGUI.showPopup(Alert.AlertType.INFORMATION, "Issues with connection", "Failed to enter chat room", "...");
                        }
                    }
                }
            } else {
                // Client is not inside main room -> Leave chat room and join main room
                // TODO dialog before leaving room
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm chat room change");
                alert.setHeaderText("Are you sure that you want to leave your current room and enter the main chat room?");
                Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent()) {
                    if(!result.get().getButtonData().isCancelButton()) {
                        boolean success = clientGUI.getCommunicationCallsFromGUI().enterChatRoom(0);
                        if(success) {
                            updateEnterRoomButton();
                        } else {
                            clientGUI.showPopup(Alert.AlertType.WARNING, "Issues with connection", "Failed to leave current chat room", "...");
                        }
                    }
                }
            }
        }
    }
}
