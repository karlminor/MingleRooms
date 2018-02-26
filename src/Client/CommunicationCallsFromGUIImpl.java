package Client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationCallsFromGUIImpl implements CommunicationCallsFromGUI {
	private Socket socket;
	private BufferedWriter output;
	public static ServerSocket ss;

	// See the interface CommunicationCallsFromGUI for descriptions/notes

	public CommunicationCallsFromGUIImpl() {
	}

	@Override
	public boolean connectToServer(InetAddress inetAddress, int port, String nickname, String avatar) {
		try {
			socket = new Socket(inetAddress, port);
			output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			output.write(nickname + "¤" + avatar + "\n");
			output.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean disconnectFromServer() {
		try {
			System.out.println("Leaving");
			output.write("Q\n");
			output.flush();
			output.close();
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean sendMessage(String message) {
		try {
			output.write("M" + message + "\n");
			output.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void move(int direction) {
		String message = "0¤0";

		switch (direction) {
		case UP:
			message = "0¤-1";
			break;
		case DOWN:
			message = "0¤1";
			break;
		case LEFT:
			message = "-1¤0";
			break;
		case RIGHT:
			message = "1¤0";
			break;
		}

		try {
			output.write("P" + message + "\n");
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean startP2PChat(int idOtherUser) {
		try {
			ss = new ServerSocket(0);
			output.write("C" + idOtherUser + "¤" + ss.getLocalPort() + "\n");
			output.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean enterChatRoom(int chatRoomNumber) {
		try {
			output.write("R" + chatRoomNumber + "¤0¤0\n");
			output.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public static ServerSocket getSS() {
		return ss;
	}
}