package Client;

public class User {
    private int id;
    private String nickname;
    private String avatarName;
    private volatile int chatRoom;
    private volatile int x;
    private volatile int y;

    public User(int id, String nickname, String avatar, int chatRoom, int x, int y) {
        this.id = id;
        this.nickname = nickname;
        this.avatarName = avatar;
        this.chatRoom = chatRoom;
        this.x = x;
        this.y = y;
    }

    public String getAvatarName() {
        return avatarName;
    }
    public String getNickname() {
        return nickname;
    }

    // The get and set methods below does not need to be synchronized since the x and y variables are volatile = thread safe
    public synchronized int getChatRoom() {
        return chatRoom;
    }
    public synchronized int getX() {
        return x;
    }
    public synchronized int getY() {
        return y;
    }
    public synchronized void setChatRoom(int chatRoom) {
        this.chatRoom = chatRoom;
    }
    public synchronized void setX(int x) {
        this.x = x;
    }
    public synchronized void setY(int y) {
        this.y = y;
    }
    public int getId(){
    	return id;
    }
}
