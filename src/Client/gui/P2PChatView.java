package Client.gui;

import Client.CommunicationCallsFromGUI;
import Client.P2PConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class P2PChatView extends HBox {
    // Size for this view only
    public static final int WIDTH = 900;
    public static final int HEIGHT = 200;

    private final int INSETS = 10;
    private final int SPACING = 10;

    private CommunicationCallsFromGUI communicationCallsFromGUI;
    private P2PConnection p2pConnection;

    private Stage stage;

    private TextArea message;
    private Button send;

    public P2PChatView(CommunicationCallsFromGUI communicationCallsFromGUI, P2PConnection p2pConnection, Stage stage) {
        this.communicationCallsFromGUI = communicationCallsFromGUI;
        this.p2pConnection = p2pConnection;
        this.stage = stage;
        stage.setOnCloseRequest(event -> send.fire());
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
        Label chatInfo = new Label("You are chatting directly with: ");

        Button disconnect = new Button("Disconnect");
        disconnect.setPrefWidth(WIDTH * 0.2);
        disconnect.setPrefHeight(HEIGHT * 0.15);
        disconnect.setOnAction(new DisconnectButtonHandler());

        sidePanel.setTop(chatInfo);
        BorderPane.setMargin(sidePanel, new Insets(0, 0, INSETS, 0));
        sidePanel.setBottom(disconnect);

        VBox centerPanel = new VBox();
        centerPanel.setPrefWidth(WIDTH * 0.8);
        centerPanel.setPrefHeight(HEIGHT * 0.60);
        centerPanel.setSpacing(SPACING);
        TextArea chat = new TextArea();
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

    private class SendMessageButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            String text = message.getText();
            if(text != null && !text.isEmpty()) {
                text = text.replaceAll("¤", "");
                if(p2pConnection.sendMessage(text)) {
                    message.setText("");
                } else {
                    showPopup(Alert.AlertType.WARNING, "Issues with connection", "Failed to send message to peer", "...");
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
