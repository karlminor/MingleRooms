package Client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class P2PConnectionImpl implements P2PConnection {
	private User user;
	private Socket socket;
	private BufferedWriter output;

	public P2PConnectionImpl(User user, Socket socket) throws IOException {
		this.user = user;
		this.socket = socket;
		output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public boolean sendMessage(String message) {
    	try {
			output.write(message + "\n");
			output.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean shutdown() {
		try {
			socket.close();
			output.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
