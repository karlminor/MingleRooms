package Client.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    private final String TITLE = "Mingle Rooms - Client";
    private final int WIDTH = 400;
    private final int HEIGHT = 300;

    private Stage stage;
    private BorderPane root;
    private FirstView firstView;
    private ChatRoomView chatRoomView;

    public final static int FIRST_VIEW = 0;
    public final static int CHAT_ROOM_VIEW = 1;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("persistent-prompt.css").toExternalForm()); // Needed for persistent prompt text fields
        stage.setScene(scene);
        stage.setTitle(TITLE);

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
                break;
        }
    }

    public void setSize(int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
    }
}
