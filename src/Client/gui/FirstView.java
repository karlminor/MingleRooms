package Client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FirstView extends VBox{
    // Size for this view only
    public static final int WIDTH = 500;
    public static final int HEIGHT = 300;

    private final int INSETS = 10;
    private final int SPACING = 10;

    private ClientGUI clientGUI;

    private PPTextField inetAddressTF;
    private PPTextField portTF;
    private PPTextField nicknameTF;

    public FirstView(ClientGUI clientGUI) {
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

        HBox splitPanel = new HBox();
        splitPanel.setAlignment(Pos.CENTER);
        splitPanel.setSpacing(SPACING);

        // Input fields
        VBox inputFieldsPanel = new VBox();
        inputFieldsPanel.setPrefWidth(WIDTH * 0.45);
        inputFieldsPanel.setSpacing(SPACING);

        HBox titleCenter = new HBox();
        titleCenter.setAlignment(Pos.CENTER);
        Label title = new Label("Mingle Rooms - Client");
        titleCenter.getChildren().add(title);

        inetAddressTF = new PPTextField("Internet address");
        portTF = new PPTextField("Port");
        nicknameTF = new PPTextField("Nickname");

        HBox buttonCenter = new HBox();
        buttonCenter.setAlignment(Pos.CENTER);
        Button connectB = new Button("Connect");
        connectB.setPrefWidth(WIDTH * 0.20);
        connectB.setOnAction(new ConnectButtonHandler());
        buttonCenter.getChildren().add(connectB);

        // TODO temp values (can be removed)
        inetAddressTF.setText("192.168.0.1");
        portTF.setText("30000");
        nicknameTF.setText("SampleNickname");

        inputFieldsPanel.getChildren().addAll(titleCenter, inetAddressTF, portTF, nicknameTF, buttonCenter);

        // Avatar selection
        VBox sidePanel = new VBox();
        sidePanel.setSpacing(SPACING);

        HBox innerSideSplitPanel = new HBox();
        innerSideSplitPanel.setSpacing(SPACING);

        HBox avatarCenter = new HBox();
        avatarCenter.setAlignment(Pos.CENTER);
        Label avatar = new Label("Avatar");
        avatarCenter.getChildren().add(avatar);

        ObservableList<String> avatars = FXCollections.observableArrayList("Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4");
        ChoiceBox<String> avatarSelection = new ChoiceBox<>(avatars);
        avatarSelection.getSelectionModel().selectFirst();

        ImageView avatarIV = new ImageView("test.jpg");
        avatarIV.setFitWidth(WIDTH * 0.2);
        avatarIV.setPreserveRatio(true);
        avatarIV.setSmooth(true);
        avatarIV.setCache(true);

        innerSideSplitPanel.getChildren().addAll(avatarSelection, avatarIV);
        sidePanel.getChildren().addAll(avatarCenter, innerSideSplitPanel);

        splitPanel.getChildren().addAll(inputFieldsPanel, sidePanel);

        getChildren().add(splitPanel);
    }

    private class ConnectButtonHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event) {
            try {
                InetAddress inetAddress = InetAddress.getByName(inetAddressTF.getText());
                int port = Integer.parseInt(portTF.getText());
                String nickname = nicknameTF.getText();

                if(inetAddress != null && port > 0 && port <= 65535 && !nickname.isEmpty()) {
                    if(!nickname.contains("¤")) {
                        boolean success = clientGUI.getCommunicationCallsFromGUI().connectToServer(inetAddress, port, nickname);
                        if(success) {
                            clientGUI.changeView(ClientGUI.CHAT_ROOM_VIEW);
                        } else {
                            // TODO show in gui
                            System.out.println("Failed to connect to server");
                        }
                    } else {
                        // TODO show in gui
                        System.out.println("Nickname contains unallowed character ¤");
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                // TODO show in gui
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // TODO show in gui
            }
        }
    }

}
