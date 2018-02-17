package Client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FirstView extends VBox{
    // Size for this view only
    public static final int WIDTH = 400;
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
        Label title = new Label("Mingle Rooms - Client");
        inetAddressTF = new PPTextField("Internet address");
        portTF = new PPTextField("Port");
        nicknameTF = new PPTextField("Nickname");
        Button connectB = new Button("Connect");
        connectB.setOnAction(new ConnectButtonHandler());

        // TODO temp values
        inetAddressTF.setText("192.168.0.1");
        portTF.setText("30000");
        nicknameTF.setText("SampleNickname");

        getChildren().addAll(title, inetAddressTF, portTF, nicknameTF, connectB);
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
                        boolean success = clientGUI.getClientCommunication().connectToServer(inetAddress, port, nickname);
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
