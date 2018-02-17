package Client;

import Client.gui.ChatRoomView;

import java.net.InetAddress;

public class CommunicationCallsFromGUIImpl implements CommunicationCallsFromGUI {
    private ChatRoomView chatRoomView;

    // See the interface CommunicationCallsFromGUI for descriptions/notes

    public CommunicationCallsFromGUIImpl(ChatRoomView chatRoomView) {
        this.chatRoomView = chatRoomView;
    }

    @Override
    public boolean connectToServer(InetAddress inetAddress, int port, String nickname) {
        // TODO connect to server
        boolean successfulConnect = true;
        if(successfulConnect) {
            // Start a new thread to handle network reads
            ClientMain.startNetworkThread(chatRoomView);
        }
        return successfulConnect;
    }

    @Override
    public boolean disconnectFromServer() {
        // TODO
        return false;
    }

    @Override
    public boolean sendMessage(String message) {
        // TODO
        return false;
    }

    @Override
    public void move(int direction) {
        // TODO
    }

    @Override
    public boolean startP2PChat(int idOtherUser) {
        // TODO
        return false;
    }

    @Override
    public boolean enterChatRoom(int chatRoomNumber) {
        // TODO
        return false;
    }
}
