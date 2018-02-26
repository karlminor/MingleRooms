package Client;

import java.net.InetAddress;
import java.net.Socket;

public interface CommunicationCallsFromGUI {
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    // GUI will call this function
    public boolean connectToServer(InetAddress inetAddress, int port, String nickname, String avatar);

    // GUI will call this function
    public boolean disconnectFromServer();

    // GUI will call this function
    public boolean sendMessage(String message);

    // GUI will call this function
    // Directions are described in the final int variables at the top of this class. (UP = 0, DOWN = 1 ...)
    // Moves this player.
    // Client must receive new character coordinates from server before the move is visible in GUI
    public void move(int direction);

    // TODO Not supported in GUI yet
    public boolean startP2PChat(int idOtherUser);

    // TODO Not supported in GUI yet
    public boolean enterChatRoom(int chatRoomNumber);
    
    public Socket getSocket();

}
