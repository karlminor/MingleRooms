package Client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FirstView extends VBox{
    private final int INSETS = 10;
    private final int SPACING = 10;

    public FirstView() {
        containerSettings();
        initComponents();
    }

    private void containerSettings() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(INSETS, INSETS, INSETS, INSETS));
        setSpacing(SPACING);
    }

    private void initComponents() {
        Label title = new Label("Mingle Rooms - Client");
        PPTextField inetAddressTF = new PPTextField("Internet address");
        PPTextField portTF = new PPTextField("Port");
        PPTextField nicknameTF = new PPTextField("Nickname");
        Button connectB = new Button("Connect");

        getChildren().addAll(title, inetAddressTF, portTF, nicknameTF, connectB);
    }

}
