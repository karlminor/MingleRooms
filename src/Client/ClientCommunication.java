package Client;

import java.net.InetAddress;

public interface ClientCommunication {
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public boolean connectToServer(InetAddress inetAddress, int port, String nickname);

    public boolean disconnectFromServer();

    public boolean sendMessage(String message);

    // Directions are described in the final int variables at the top of this class. (UP = 0, DOWN = 1 ...)
    // Moves this player.
    public void move(int direction);

}
