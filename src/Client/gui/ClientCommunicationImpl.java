package Client.gui;

import Client.ClientCommunication;
import Client.ClientMain;

import java.net.InetAddress;

public class ClientCommunicationImpl implements ClientCommunication{
    private ChatRoomView chatRoomView;

    public ClientCommunicationImpl(ChatRoomView chatRoomView) {
        this.chatRoomView = chatRoomView;
    }

    @Override
    public boolean connectToServer(InetAddress inetAddress, int port, String nickname) {
        // TODO connect to server
        boolean successfulConnect = true;
        if(successfulConnect) {
            ClientMain.startNetworkThread(chatRoomView);
        }
        return successfulConnect;
    }

    @Override
    public boolean disconnectFromServer() {
        return false;
    }

    @Override
    public boolean sendMessage(String message) {
        return false;
    }

    @Override
    public void move(int direction) {

    }
}
