package Client;

public interface P2PConnection {

    /**
        Get user object linked to the peer
     **/
    public User getUser();

    /**
     Send message to peer
     **/
    public boolean sendMessage(String message);

    /**
     Tell peer to shutdown connection. Also terminate this side of the connection.
     **/
    public boolean shutdown();

}
