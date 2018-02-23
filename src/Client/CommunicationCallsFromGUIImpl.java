package Client;

import Client.gui.ChatRoomView;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class CommunicationCallsFromGUIImpl implements CommunicationCallsFromGUI {
    private ChatRoomView chatRoomView;
    private Socket socket;
    private BufferedWriter output;

    // See the interface CommunicationCallsFromGUI for descriptions/notes

    public CommunicationCallsFromGUIImpl(ChatRoomView chatRoomView) {
        this.chatRoomView = chatRoomView;
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
    		output.write("Q\n");
    		output.flush();
			output.close();
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
    public P2PConnection startP2PChat(int idOtherUser) {
        // TODO
        return null;
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
}