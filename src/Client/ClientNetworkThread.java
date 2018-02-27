package Client;

import Client.gui.ChatRoomView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientNetworkThread extends Thread {
	private ChatRoomView chatRoomView;
	private volatile ArrayList<User> allUsers; // volatile = thread safe
	private volatile ArrayList<User> sameRoomUsers;
	private volatile ArrayList<String> chatMessages;
	private volatile ArrayList<String> history;
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
		history = new ArrayList<>();
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
			} catch (IOException|NullPointerException e) {
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
	public void decodeMessage(String message) throws IOException, NullPointerException {
		String msg[];
		char identifier = message.charAt(0);
		message = message.substring(1);
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
				int room = u.getChatRoom();
				u.setChatRoom(Integer.valueOf(msg[1]));
				u.setX(Integer.valueOf(msg[2]));
				u.setY(Integer.valueOf(msg[3]));

				if (u == myUser) {
					sameRoomUsers.clear();
					chatMessages.clear();
					findSameRoomUsers();
				} else if (u.getChatRoom() == myUser.getChatRoom()) {
					sameRoomUsers.add(u);
					if (room == 0) {
						history.add(u.getNickname() + " (" + u.getId() + ") has joined room " + u.getChatRoom());
					} else {
						history.add(u.getNickname() + " (" + u.getId() + ") has left room " + room);
					}
				} else {
					sameRoomUsers.remove(u);
					if (room == 0) {
						history.add(u.getNickname() + " (" + u.getId() + ") has joined room " + u.getChatRoom());
					} else {
						history.add(u.getNickname() + " (" + u.getId() + ") has left room " + room);
					}
				}

				updateFriendsOnline();
				updateGUICharacters();
				updateGUIChat();
				updateHistory();
			}

			break;
		case ('N'):
			msg = message.split("¤");
			User newUser = new User(Integer.valueOf(msg[1]), msg[2], msg[3], Integer.valueOf(msg[4]),
					Integer.valueOf(msg[5]), Integer.valueOf(msg[6]));
			allUsers.add(newUser);
			history.add(newUser.getNickname() + " (" + newUser.getId() + ") has joined Main Room");
			if (myUser.getChatRoom() == newUser.getChatRoom()) {
				sameRoomUsers.add(newUser);
				updateGUICharacters();
				updateFriendsOnline();
			}
			updateHistory();
			break;
		case ('M'):
			msg = message.split("¤");
			u = findUserWithId(Integer.valueOf(msg[0]));
			if (u != null) {
				chatMessages.add(msg[1].substring(0, 9) + u.getNickname() + " (" + u.getId() + "): " + msg[1].substring(9));
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
					history.add(u.getNickname() + " (" + u.getId() + ") has left");
					updateFriendsOnline();
					updateGUICharacters();
					updateHistory();
				}
			}
			break;
		case ('C'):
			msg = message.split("¤");
			Socket p2pSocket;
			if (Integer.valueOf(msg[0]) == myUser.getId()) {
				ServerSocket ss = CommunicationCallsFromGUIImpl.getSS();
				p2pSocket = ss.accept();
				ss.close();
				u = findUserWithId(Integer.valueOf(msg[1]));
			} else {
				p2pSocket = new Socket(msg[1].substring(1), Integer.valueOf(msg[2]));
				u = findUserWithId(Integer.valueOf(msg[0]));
			}

			if (u != null) {
				startP2PConnection(new P2PConnectionImpl(u, myUser, p2pSocket));
			}
			break;
		default:
			break;
		}

	}

	public void startP2PConnection(P2PConnectionImpl p2p) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatRoomView.startP2PConnection(p2p);
			}
		});
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
				chatRoomView.displayCharactersInGUI(sameRoomUsers, allUsers);
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

	public void updateHistory() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatRoomView.updateChatRoomJoinLeaveHistory(history);
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
		findSameRoomUsers();
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
