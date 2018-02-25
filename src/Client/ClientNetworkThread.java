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
	private ChatRoomView chatRoomView;
	private volatile ArrayList<User> allUsers; // volatile = thread safe
	private volatile ArrayList<User> sameRoomUsers;
	private volatile ArrayList<String> chatMessages;
	private BufferedReader input;
	private Socket socket;
	private User myUser;
	private Alert alert;

	public ClientNetworkThread(ChatRoomView chatRoomView, Socket socket, Alert alert) {
		this.alert = alert;
		this.chatRoomView = chatRoomView;
		chatMessages = new ArrayList<>();
		allUsers = new ArrayList<>();
		sameRoomUsers = new ArrayList<>();
		this.socket = socket;
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
				alert.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL); // Must add cancel button before close
																					// can be called for some reason
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

			if (isInterrupted()) {
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
		String msg[];
		char identifier = message.charAt(0);
		message = message.substring(1);
		System.out.println(identifier + " " + message);
		User u;
		switch (identifier) {
		case ('P'):
			msg = message.split("¤");
			u = findUserWithId(Integer.valueOf(msg[0]));

			if (u != null) {
				u.setX(Integer.valueOf(msg[1]));
				u.setY(Integer.valueOf(msg[2]));
				updateGUICharacters();
			}
			break;
		case ('R'):
			msg = message.split("¤");
			u = findUserWithId(Integer.valueOf(msg[0]));
			if (u != null) {
				u.setChatRoom(Integer.valueOf(msg[1]));
				u.setX(Integer.valueOf(msg[2]));
				u.setY(Integer.valueOf(msg[3]));

				if (u == myUser) {
					sameRoomUsers.clear();
					chatMessages.clear();
					findSameRoomUsers();
				} else if (u.getChatRoom() == myUser.getChatRoom()) {
					sameRoomUsers.add(u);
				} else {
					sameRoomUsers.remove(u);
				}
				updateFriendsOnline();
				updateGUICharacters();
				updateGUIChat();
			}

			break;
		case ('N'):
			msg = message.split("¤");
			User newUser = new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]),
					Integer.valueOf(msg[5]), Integer.valueOf(msg[6]));
			allUsers.add(newUser);
			if (myUser.getChatRoom() == newUser.getChatRoom()) {
				sameRoomUsers.add(newUser);
				updateGUICharacters();
				updateFriendsOnline();
			}
			break;
		case ('M'):
			msg = message.split("¤");
			u = findUserWithId(Integer.valueOf(msg[0]));
			if (u != null) {
				chatMessages.add(u.getNickname() + ": " + msg[1]);
				updateGUIChat();
			}
			break;
		case ('Q'):
			if (Integer.valueOf(message) == myUser.getId()) {
				socket.close();
				input.close();
				this.interrupt();
			} else {
				u = findUserWithId(Integer.valueOf(message));
				if (u != null) {
					allUsers.remove(u);
					sameRoomUsers.remove(u);
					updateFriendsOnline();
					updateGUICharacters();
				}
			}
			break;
		case ('C'):
			msg = message.split("¤");
			// Peer-to-peer
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
				chatRoomView.displayCharactersInGUI(sameRoomUsers);
			}
		});
	}

	public void updateFriendsOnline() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatRoomView.updateFriendsOnline(sameRoomUsers);
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
		myUser = new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]), Integer.valueOf(msg[5]),
				Integer.valueOf(msg[6]));
		allUsers.add(myUser);

		Platform.runLater(() -> {
			chatRoomView.setClient(myUser);
		});

		int length = Integer.parseInt(input.readLine());
		while (length != 0) {
			message = input.readLine();
			msg = message.split("¤");
			allUsers.add(new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]),
					Integer.valueOf(msg[5]), Integer.valueOf(msg[6])));
			length--;
		}
		sameRoomUsers.addAll(allUsers);
		updateFriendsOnline();
		updateGUICharacters();
	}

	private User findUserWithId(int id) {
		for (User u : allUsers) {
			if (u.getId() == id) {
				return u;
			}
		}
		return null;
	}

	private void findSameRoomUsers() {
		for (User u : allUsers) {
			if (u.getChatRoom() == myUser.getChatRoom()) {
				sameRoomUsers.add(u);
			}
		}
	}
}
