package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import Client.gui.P2PChatView;
import javafx.application.Platform;

public class P2PConnectionImpl implements P2PConnection {
	private User user;
	private Socket socket;
	private BufferedWriter output;
	private BufferedReader input;
	private P2PChatView p2pChatView;
	private ArrayList<String> messages;
	private messageReader msgr;

	public P2PConnectionImpl(User user, Socket socket) throws IOException {
		this.user = user;
		this.socket = socket;
		output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		messages = new ArrayList<>();
		msgr = new messageReader();
		msgr.start();
		
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public boolean sendMessage(String message) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    	try {
    		String msg = user.getNickname() + ": " + dtf.format(LocalDateTime.now()) + " " + message;
    		messages.add(msg);
			output.write("M" + msg + "\n");
			output.flush();
			msgr.updateGUIChat();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean shutdown() {
		try {
			output.write("Q\n");
			output.flush();
			input.close();
			output.close();
			socket.close();
			msgr.interrupt();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void setP2PChatView(P2PChatView p2pChatView) {
		this.p2pChatView = p2pChatView;
	}
	
	private class messageReader extends Thread{
		
		public void run() {
			String message;
			while (true) {
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
		
		public String read() throws IOException {
			return input.readLine();
		}

		public void decodeMessage(String message) throws IOException, NullPointerException{
			char identifier = message.charAt(0);
			message = message.substring(1);
			switch (identifier) {
			case ('M'):
				messages.add(message);
				updateGUIChat();
				break;
			case ('Q'):
				input.close();
				output.close();
				socket.close();
				this.interrupt();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						p2pChatView.close();
					}
				});
				break;
			default:
				break;
			}
		}
		
		public void updateGUIChat() {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					p2pChatView.updateChat(messages);
				}
			});
		}
	}
}

