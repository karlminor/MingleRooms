package Client.gui;

import Client.User;
import javafx.application.Platform;

import java.util.ArrayList;

public class ClientNetworkThread extends Thread {
    private ChatRoomView chatRoomView;
    private volatile ArrayList<User> users;
    private volatile ArrayList<String> chatMessages;

    public ClientNetworkThread(ChatRoomView chatRoomView) {
        this.chatRoomView = chatRoomView;
        chatMessages = new ArrayList<>();
        users = new ArrayList<>();

        // TODO temporary code for testing
        users.add(new User(1, "Pelle", "test.jpg", 1, 3, 4));
        users.add(new User(1, "Nisse", "test.jpg", 1, 0, 0));
        chatMessages.add("18:32:15 Pelle: Hejsan!");
        chatMessages.add("18:32:28 Nisse: Tjena Pelle!");
        chatMessages.add("18:32:42 Pelle: Hur är läget?");
        chatMessages.add("18:32:50 Nisse: Det är bra!");
    }

    public void run() {
        // TODO temporary code for testing
        updateGUIChat(chatMessages);
        updateFriendsOnline(users);

        int i = 0;
        while(true) {
            String message = read();
            decodeMessage(message);

            // TODO temporary code for testing
            users.get(0).setX((int) (Math.random() * 5));
            users.get(0).setY((int) (Math.random() * 5));
            updateGUICharacters(users);
            i++;
            chatMessages.add("18:32:50 ----: Scroll test" + i);
            updateGUIChat(chatMessages);
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
    // The method that is called in the GUI will handle clearing the screen and rendering
    public void updateGUICharacters(ArrayList<User> users){
        // This will add an update to the GUI in the GUI thread's que. GUI will handle update when there is time.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoomView.displayCharactersInGUI(users);
            }
        });
    }

    public void updateFriendsOnline(ArrayList<User> users) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoomView.updateFriendsOnline(users);
            }
        });
    }

    public void updateGUIChat(ArrayList<String> chatMessages) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoomView.updateChat(chatMessages);
            }
        });
    }
}
