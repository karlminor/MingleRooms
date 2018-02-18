package Client;

import Client.gui.ChatRoomView;
import javafx.application.Platform;

import java.util.ArrayList;

public class ClientNetworkThread extends Thread {
    private ChatRoomView chatRoomView;
    private volatile ArrayList<User> users; // volatile = thread safe
    private volatile ArrayList<String> chatMessages;

    public ClientNetworkThread(ChatRoomView chatRoomView) {
        this.chatRoomView = chatRoomView;
        chatMessages = new ArrayList<>();
        users = new ArrayList<>();

        // TODO temporary code for testing
        users.add(new User(1, "Pelle", "avatar1.jpg", 1, 3, 4));
        users.add(new User(1, "Nisse", "avatar1.jpg", 1, 0, 0));
        chatMessages.add("18:32:15 Pelle: Hejsan!");
        chatMessages.add("18:32:28 Nisse: Tjena Pelle!");
        chatMessages.add("18:32:42 Pelle: Hur är läget?");
        chatMessages.add("18:32:50 Nisse: Det är bra!");
        // TODO -------------------------------------------
    }

    public void run() {
        // TODO temporary code for testing (can be disabled)
        updateGUIChat(chatMessages);
        updateFriendsOnline(users);
        // TODO -------------------------------------------

        int i = 0;
        while(true) {
            String message = read();
            decodeMessage(message);

            // TODO temporary code for testing (can be disabled)
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
            // TODO -------------------------------------------
        }
    }

    // Read messages from server
    public String read() {
        // TODO
        return null;
    }

    // Check the Server.UserLogic.Users for all message types and how to differentiate them
    public void decodeMessage(String message) {
        // TODO
        // e.g. run displayCharactersInGUI() if message received was a character move update
    }

    // Call this method everytime a user moves
    // The method that is called in the GUI will handle clearing the screen and rendering
    public void updateGUICharacters(ArrayList<User> users){
        // This will add an update to the GUI in the GUI thread's que. GUI will handle update when there is time.
        // https://stackoverflow.com/questions/13784333/platform-runlater-and-task-in-javafx
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
