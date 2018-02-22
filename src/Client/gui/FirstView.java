package Client.gui;

import Client.CommunicationCallsFromGUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FirstView extends VBox{
    // Size for this view only
    public static final int WIDTH = 500;
    public static final int HEIGHT = 300;

    private final int INSETS = 10;
    private final int SPACING = 10;

    private static final String AVATAR_FOLDER_PATH_FOR_FILE = "res/avatars/";
    public final static String AVATAR_FOLDER_PATH_FOR_JAVAFX = "file:res/avatars/";

    private ArrayList<String> availableAvatars;
    private ArrayList<String> availableAvatarsNoEnding;

    private ClientGUI clientGUI;

    private PPTextField inetAddressTF;
    private PPTextField portTF;
    private PPTextField nicknameTF;
    private ChoiceBox<String> avatarSelection;
    private ImageView avatarIV = null;
    private Button connectB;

    public FirstView(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;

        availableAvatars = getAvailableAvatars();
        availableAvatarsNoEnding = getAvailableAvatarsNoEnding();

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
        inetAddressTF.setOnKeyPressed(new KeyboardHandler());
        portTF = new PPTextField("Port");
        portTF.setOnKeyPressed(new KeyboardHandler());
        nicknameTF = new PPTextField("Nickname");
        nicknameTF.setOnKeyPressed(new KeyboardHandler());

        HBox buttonCenter = new HBox();
        buttonCenter.setAlignment(Pos.CENTER);
        connectB = new Button("Connect");
        connectB.setPrefWidth(WIDTH * 0.20);
        connectB.setOnAction(new ConnectButtonHandler());
        buttonCenter.getChildren().add(connectB);

        // TODO temp values (can be removed)
        inetAddressTF.setText("localhost");
        portTF.setText("30000");
        nicknameTF.setText("Nickname");

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

        ObservableList<String> avatars = FXCollections.observableArrayList(availableAvatarsNoEnding);
        avatarSelection = new ChoiceBox<>(avatars);
        avatarSelection.getSelectionModel().selectFirst();
        avatarSelection.setOnAction(new AvatarSelectionHandler());
        avatarSelection.setOnKeyPressed(new KeyboardHandler());

        try {
            String firstImagePath = AVATAR_FOLDER_PATH_FOR_JAVAFX + availableAvatars.get(0);
            avatarIV = new ImageView(firstImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(avatarIV != null) {
            avatarIV.setFitWidth(WIDTH * 0.2);
            avatarIV.setPreserveRatio(true);
            avatarIV.setSmooth(true);
            avatarIV.setCache(true);
            innerSideSplitPanel.getChildren().addAll(avatarSelection, avatarIV);
        } else {
            innerSideSplitPanel.getChildren().addAll(avatarSelection);
        }

        sidePanel.getChildren().addAll(avatarCenter, innerSideSplitPanel);

        splitPanel.getChildren().addAll(inputFieldsPanel, sidePanel);

        getChildren().add(splitPanel);
    }

    public static ArrayList<String> getAvailableAvatars() {
        File imageFolder = new File(AVATAR_FOLDER_PATH_FOR_FILE);
        File[] listOfFiles = imageFolder.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        if(listOfFiles != null) {
            for(File f : listOfFiles) {
                fileNames.add(f.getName());
            }
        } else {
            System.out.println("Found no avatar images");
        }
        return fileNames;
    }

    public static ArrayList<String> getAvailableAvatarsNoEnding() {
        File imageFolder = new File(AVATAR_FOLDER_PATH_FOR_FILE);
        File[] listOfFiles = imageFolder.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        if(listOfFiles != null) {
            for(File f : listOfFiles) {
                fileNames.add(f.getName().split("[.]")[0]);
            }
        } else {
            System.out.println("Found no avatar images");
        }
        return fileNames;
    }

    private void changeAvatarImage(int index) {
        if(index < availableAvatars.size()) {
            String avatarFileName = availableAvatars.get(index);
            avatarIV.setImage(new Image(AVATAR_FOLDER_PATH_FOR_JAVAFX + avatarFileName));
        }
    }

    private class AvatarSelectionHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if(event.getSource() instanceof ChoiceBox) {
                int selectedIndex = avatarSelection.getSelectionModel().getSelectedIndex();
                changeAvatarImage(selectedIndex);
            }
        }
    }

    private class KeyboardHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case ENTER:
                    System.out.println("ENTER");
                    connectB.fire();
                    break;
            }
            event.consume();
        }
    }

    private class ConnectButtonHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event) {
            try {
                InetAddress inetAddress = InetAddress.getByName(inetAddressTF.getText());
                int port = Integer.parseInt(portTF.getText());
                String nickname = nicknameTF.getText();
                String avatar = availableAvatars.get(avatarSelection.getSelectionModel().getSelectedIndex());
                if(inetAddress != null) {
                    if(port >= 0 && port <= 65535) {
                        if(!nickname.isEmpty()) {
                            if(!nickname.contains("¤")) {
                                if(avatar != null && !avatar.isEmpty()) {
                                    boolean success = clientGUI.getCommunicationCallsFromGUI().connectToServer(inetAddress, port, nickname, avatar);
                                    if(success) {
                                        clientGUI.changeView(ClientGUI.CHAT_ROOM_VIEW);
                                    } else {
                                        clientGUI.showPopup(Alert.AlertType.WARNING, "Issues with connection", "Failed to connect to server", "...");
                                    }
                                } else {
                                    clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with input parameters", "Invalid or no avatar selected", "Please select a valid avatar.");
                                }
                            } else {
                                clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with input parameters", "Nickname field contains illegal characters", "Please remove '¤' from the nickname.");
                            }
                        } else {
                            clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with input parameters", "Nickname field is empty", "Please enter a nickname to continue.");
                        }
                    } else {
                        clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with input parameters", "Entered port is out of range", "Please enter a within the interval 0-65535.");
                    }
                } else {
                    clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with input parameters", "Problem with internet address", "...");
                }
            } catch (UnknownHostException e) {
                clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with input parameters", "Problem with internet address", "Entered host is unknown.");
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                clientGUI.showPopup(Alert.AlertType.WARNING, "Issue with input parameters", "Illegal port format", "Please make sure that the port does not contain alphabetic characters.");
            }
        }
    }

}
