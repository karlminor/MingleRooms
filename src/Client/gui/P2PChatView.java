package Client.gui;

import java.util.ArrayList;
import Client.P2PConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class P2PChatView extends HBox {
    // Size for this view only
    public static final int WIDTH = 900;
    public static final int HEIGHT = 200;

    private final int INSETS = 10;
    private final int SPACING = 10;

    private P2PConnection p2pConnection;

    private Stage stage;

    private TextArea message;
    private TextArea chat;
    private Button send;
    private Button disconnectB;

    public P2PChatView(P2PConnection p2pConnection, Stage stage) {
        this.p2pConnection = p2pConnection;
        this.stage = stage;
        stage.setOnCloseRequest(event -> disconnectB.fire());
        containerSettings();
        initComponents();
    }

    private void containerSettings() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(INSETS, INSETS, INSETS, INSETS));
        setSpacing(SPACING);
    }

    private void initComponents() {
        BorderPane sidePanel = new BorderPane();
        sidePanel.setPrefWidth(WIDTH * 0.2);
        Label chatInfo = new Label("You are chatting directly with: " + p2pConnection.getUser().getNickname() + " (" + p2pConnection.getUser().getId() + ")");
        chatInfo.setWrapText(true);

        try {
            String imagePath = FirstView.AVATAR_FOLDER_PATH_FOR_JAVAFX + p2pConnection.getUser().getAvatarName();
            ImageView avatarIV = new ImageView(imagePath);

            avatarIV.setFitWidth(WIDTH * 0.10);
            avatarIV.setFitHeight(WIDTH * 0.10);
            avatarIV.setPreserveRatio(false);
            avatarIV.setSmooth(true);
            avatarIV.setCache(true);

            sidePanel.setCenter(avatarIV);
        } catch (Exception e) {
            e.printStackTrace();
        }

        disconnectB = new Button("Disconnect");
        disconnectB.setPrefWidth(WIDTH * 0.2);
        disconnectB.setPrefHeight(HEIGHT * 0.15);
        disconnectB.setOnAction(new DisconnectButtonHandler());

        sidePanel.setTop(chatInfo);
        BorderPane.setMargin(sidePanel, new Insets(0, 0, INSETS, 0));
        sidePanel.setBottom(disconnectB);

        VBox centerPanel = new VBox();
        centerPanel.setPrefWidth(WIDTH * 0.8);
        centerPanel.setPrefHeight(HEIGHT * 0.60);
        centerPanel.setSpacing(SPACING);
        chat = new TextArea();
        chat.setOnKeyPressed(new MessageKeyboardHandler());
        chat.setPrefHeight(HEIGHT * 0.50);
        chat.setWrapText(true);
        chat.setPromptText("No chat messages received");
        chat.setEditable(false);

        HBox innerLowerPanel = new HBox();
        innerLowerPanel.setPrefHeight(HEIGHT * 0.40);
        innerLowerPanel.setSpacing(SPACING);
        message = new TextArea();
        message.setOnKeyPressed(new MessageKeyboardHandler());
        message.setPrefWidth(WIDTH * 0.65);
        message.setWrapText(true);
        message.setPromptText("Enter a chat message");
        send = new Button("Send");
        send.setOnAction(new SendMessageButtonHandler());
        send.setPrefWidth(WIDTH * 0.15);
        send.setPrefHeight(HEIGHT * 0.40);

        innerLowerPanel.getChildren().addAll(message, send);

        centerPanel.getChildren().addAll(chat, innerLowerPanel);

        getChildren().addAll(sidePanel, centerPanel);
    }

    private void showPopup(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    
    public void close() {
    	showPopup(Alert.AlertType.WARNING, "Issues with connection", "Peer disconnected", "...");
    	stage.close();
    }
    
    public void updateChat(ArrayList<String> messages) {
        chat.clear();
        for(String s : messages) {
            chat.setText(chat.getText() + s + "\n");
        }
        chat.setScrollTop(Double.MAX_VALUE);
    }

    private class SendMessageButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            String text = message.getText();
            if(text != null && !text.isEmpty()) {
                text = text.replaceAll("¤", "");
                if(!text.isEmpty()) {
                    if(p2pConnection.sendMessage(text)) {
                        message.setText("");
                    } else {
                        showPopup(Alert.AlertType.WARNING, "Issues with connection", "Failed to send message to peer", "...");
                    }
                }
            }
        }
    }

    private class DisconnectButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if(!p2pConnection.shutdown()) {
               showPopup(Alert.AlertType.WARNING, "Issues with connection", "Failed to disconnect from peer", "...");
            }
            stage.close();
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
}
