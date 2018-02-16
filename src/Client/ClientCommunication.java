package Client;

import java.net.InetAddress;

public interface ClientCommunication {

    public boolean connectToServer(InetAddress inetAddress, int port, String nickname);

    public boolean disconnectFromServer();

    public boolean sendMessage(String message);

}
