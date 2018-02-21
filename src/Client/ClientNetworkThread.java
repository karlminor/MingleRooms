package Client;

import Client.gui.ChatRoomView;
import Client.gui.ClientGUI;
import Server.MessageLogic.Message;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ClientNetworkThread extends Thread {
    private ClientGUI clientGUI;
    private ChatRoomView chatRoomView;
    private volatile ArrayList<User> users; // volatile = thread safe
    private volatile ArrayList<String> chatMessages;
    private BufferedReader input;
    

    public ClientNetworkThread(ChatRoomView chatRoomView, ClientGUI clientGUI) {
    	this.chatRoomView = chatRoomView;
        this.clientGUI = clientGUI;
        chatMessages = new ArrayList<>();
        users = new ArrayList<>();
    	
        try {
   		 input = new BufferedReader(new InputStreamReader(clientGUI.getCommunicationCallsFromGUI().getSocket().getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }

    public void run() {
        while(true) {
            String message;
			try {
				message = read();
				decodeMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
    }

    /**
     * Read messages from server.
     * @throws IOException 
     */
    public String read() throws IOException {
    	return input.readLine();
    }

    /**
     * Check the Server.UserLogic.Users for all message types and how to differentiate them.
     */
    public void decodeMessage(String message) {
        // TODO
        // e.g. run displayCharactersInGUI() if message received was a character move update
    	String msg[];
    	char identifier = message.charAt(0);
        message = message.substring(1);
        System.out.println(identifier);
        System.out.println(message);
        switch (identifier) {
            case ('P'):
                msg = message.split("¤");
                break;
            case ('R'):
                msg = message.split("¤");
                break;
            case ('M'):
            	msg = message.split("¤");
            	chatMessages.add(msg[0] + ": " + msg[1]);
                chatRoomView.updateChat(chatMessages);
                break;
            case ('Q'):
                break;
            default:
                break;
        }
    }

    /**
     * Call this method everytime a user moves.
     * The method that is called in the GUI will handle clearing the screen and rendering.
     */
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
