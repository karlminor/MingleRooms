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
   		setup();
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
        System.out.println(message);
        switch (identifier) {
            case ('P'):
            	msg = message.split("¤");
            	for(User u: users){
            		if(u.getId()==Integer.valueOf(msg[0])){
            			u.setX(Integer.valueOf(msg[1]));
            			u.setY(Integer.valueOf(msg[2]));
            		}
            	}
            	updateGUICharacters();
                break;
            case ('R'):
                msg = message.split("¤");
                break;
            case ('N'):
            	msg = message.split("¤");
            	users.add(new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]), Integer.valueOf(msg[5]), Integer.valueOf(msg[6])));
                updateGUICharacters();
            	break;
            case ('M'):
            	msg = message.split("¤");
            	chatMessages.add(msg[0] + ": " + msg[1]);
                updateGUIChat();
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
    public void updateGUICharacters(){
        // This will add an update to the GUI in the GUI thread's que. GUI will handle update when there is time.
        // https://stackoverflow.com/questions/13784333/platform-runlater-and-task-in-javafx
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoomView.displayCharactersInGUI(users);
            }
        });
    }

    public void updateFriendsOnline() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoomView.updateFriendsOnline(users);
            }
        });
    }

    public void updateGUIChat() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatRoomView.updateChat(chatMessages);
            }
        });
    }
    
    private void setup() throws IOException{
    	String message = input.readLine();
    	String msg[] = message.split("¤");
        User client = new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]), Integer.valueOf(msg[5]), Integer.valueOf(msg[6]));
    	users.add(client);

    	Platform.runLater(() -> {chatRoomView.setClient(client);});

    	
    	int length = Integer.parseInt(input.readLine());
    	while(length != 0){
    		message = input.readLine();
    		msg = message.split("¤");
    		users.add(new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]), Integer.valueOf(msg[5]), Integer.valueOf(msg[6])));
    		length--;
    	}
    	updateFriendsOnline();
    	updateGUICharacters();
    }
}
