package Client;

import Client.gui.ChatRoomView;
import Client.gui.ClientGUI;
import Server.MessageLogic.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ClientNetworkThread extends Thread {
	private ClientGUI clientGUI;
	private ChatRoomView chatRoomView;
	private volatile ArrayList<User> users; // volatile = thread safe
	private volatile ArrayList<String> chatMessages;
	private BufferedReader input;
	private Socket socket;
	private int id;
	private Alert alert;

	public ClientNetworkThread(ChatRoomView chatRoomView, ClientGUI clientGUI, Alert alert) {
		this.alert = alert;
		this.chatRoomView = chatRoomView;
		this.clientGUI = clientGUI;
		chatMessages = new ArrayList<>();
		users = new ArrayList<>();
		socket = clientGUI.getCommunicationCallsFromGUI().getSocket();
	}

	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			setup();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Close GUI alert window
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				alert.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL); // Must add cancel button before close can be called for some reason
				alert.close();
			}
		});

		while (true) {
			String message;
			try {
				message = read();
				decodeMessage(message);
			} catch (IOException e) {
			}

			if(isInterrupted()) {
				// Shutdown thread
				return;
			}
		}

	}

	/**
	 * Read messages from server.
	 * 
	 * @throws IOException
	 */
	public String read() throws IOException {
		return input.readLine();
	}

	/**
	 * Check the Server.UserLogic.Users for all message types and how to
	 * differentiate them.
	 * 
	 * @throws IOException
	 */
	public void decodeMessage(String message) throws IOException {
		// TODO
		// e.g. run displayCharactersInGUI() if message received was a character move
		// update
		String msg[];
		char identifier = message.charAt(0);
		message = message.substring(1);
		System.out.println(identifier + " " + message);
		switch (identifier) {
		case ('P'):
			msg = message.split("¤");
			for (User u : users) {
				if (u.getId() == Integer.valueOf(msg[0])) {
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
			users.add(new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]),
					Integer.valueOf(msg[5]), Integer.valueOf(msg[6])));
			updateGUICharacters();
			updateFriendsOnline();
			break;
		case ('M'):
			msg = message.split("¤");
			chatMessages.add(msg[0] + ": " + msg[1]);
			updateGUIChat();
			break;
		case ('Q'):
			if (Integer.valueOf(message) == id) {
				socket.close();
				input.close();
				this.interrupt();
			} else {
				for (User u : users) {
					if (u.getId() == Integer.valueOf(message)) {
						users.remove(u);
						updateFriendsOnline();
						updateGUICharacters();
						break;
					}
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Call this method everytime a user moves. The method that is called in the GUI
	 * will handle clearing the screen and rendering.
	 */
	public void updateGUICharacters() {
		// This will add an update to the GUI in the GUI thread's que. GUI will handle
		// update when there is time.
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

	private void setup() throws IOException {
		String message = input.readLine();
		String msg[] = message.split("¤");
		id = Integer.valueOf(msg[1]);
		User client = new User(id, msg[2], msg[3], Integer.valueOf(msg[4]), Integer.valueOf(msg[5]),
				Integer.valueOf(msg[6]));
		users.add(client);

		Platform.runLater(() -> {
			chatRoomView.setClient(client);
		});

		int length = Integer.parseInt(input.readLine());
		while (length != 0) {
			message = input.readLine();
			msg = message.split("¤");
			users.add(new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]),
					Integer.valueOf(msg[5]), Integer.valueOf(msg[6])));
			length--;
		}
		updateFriendsOnline();
		updateGUICharacters();
	}
}
