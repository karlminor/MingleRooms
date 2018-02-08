package Client.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    private final String TITLE = "Mingle Rooms - Client";
    private final int WIDTH = 400;
    private final int HEIGHT = 300;

    private BorderPane root;
    private FirstView firstView;

    @Override
    public void start(Stage stage) throws Exception {
        root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("persistent-prompt.css").toExternalForm()); // Needed for persistent prompt text fields
        stage.setScene(scene);
        stage.setTitle(TITLE);

        stage.show();
        firstView = new FirstView();
        changeView(firstView);
    }

    private void changeView(Pane newPane) {
        root.getChildren().removeAll();
        root.setCenter(newPane);
    }
}
