package Client.gui;

import Client.ClientMain;
import Client.CommunicationCallsFromGUI;
import Client.CommunicationCallsFromGUIImpl;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientGUI extends Application {
    private final String TITLE = "Mingle Rooms - Client";
    private final int WIDTH = 400;
    private final int HEIGHT = 300;

    private Stage stage;
    private BorderPane root;
    private FirstView firstView;
    private ChatRoomView chatRoomView;
    private CommunicationCallsFromGUI communicationCallsFromGUI;

    public final static int FIRST_VIEW = 0;
    public final static int CHAT_ROOM_VIEW = 1;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("persistent-prompt.css").toExternalForm()); // Needed for persistent prompt text fields
        scene.getStylesheets().add(getClass().getResource("boardTiles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

        stage.show();
        firstView = new FirstView(this);
        changeView(FIRST_VIEW);
        chatRoomView = new ChatRoomView(this);
    }

    public void changeView(int chatRoom) {
        root.getChildren().removeAll();
        switch (chatRoom) {
            case FIRST_VIEW:
                setSize(FirstView.WIDTH, FirstView.HEIGHT);
                root.setCenter(firstView);
                break;
            case CHAT_ROOM_VIEW:
                root.setCenter(chatRoomView);
                setSize(ChatRoomView.WIDTH, ChatRoomView.HEIGHT);
                ClientMain.startNetworkThread(chatRoomView);
                break;
        }
    }

    public void setSize(int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
    }

    public CommunicationCallsFromGUI getCommunicationCallsFromGUI() {
        if(communicationCallsFromGUI == null) {
            communicationCallsFromGUI = new CommunicationCallsFromGUIImpl(chatRoomView);
        }
        return communicationCallsFromGUI;
    }

    public void showPopup(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
