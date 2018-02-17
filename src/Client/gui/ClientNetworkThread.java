package Client.gui;

import Client.User;
import javafx.application.Platform;

import java.util.ArrayList;

public class ClientNetworkThread extends Thread {
    private ChatRoomView chatRoomView;
    private volatile ArrayList<User> users;

    public ClientNetworkThread(ChatRoomView chatRoomView) {
        this.chatRoomView = chatRoomView;
        users = new ArrayList<>();

        // TODO temporary code for testing
        users.add(new User(1, "User 1", "test.jpg", 3, 4));
    }

    public void run() {
        while(true) {
            String message = read();
            decodeMessage(message);

            // TODO temporary code for testing
            users.get(0).setX((int) (Math.random() * 5));
            users.get(0).setY((int) (Math.random() * 5));
            displayCharactersInGUI(users);
            try {
                Thread.sleep(700);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Read messages from server
    public String read() {
        // TODO
        return null;
    }

    public void decodeMessage(String message) {
        // TODO
        // e.g. run displayCharactersInGUI() if message received was a character move update
    }

    // Call this method everytime a user moves
    public void displayCharactersInGUI(ArrayList<User> users){
        // This will add an update to the GUI in the GUI thread's que. GUI will handle update when there is time.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoomView.displayCharactersInGUI(users);
            }
        });
    }
}
