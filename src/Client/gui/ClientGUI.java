package Client.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    private final String TITLE = "Mingle Rooms";
    private final int WIDTH = 400;
    private final int HEIGHT = 300;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.show();
    }
}
