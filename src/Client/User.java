package Client;

import Client.gui.FirstView;
import javafx.scene.image.Image;

public class User {
    private int id;
    private String nickname;
    private String avatarImage;
    private Image avatar;
    private volatile int chatRoom;
    private volatile int x;
    private volatile int y;

    public User(int id, String nickname, String avatarImage, int chatRoom, int x, int y) {
        this.id = id;
        this.nickname = nickname;
        this.avatarImage = avatarImage;
        try {
            avatar = new Image(FirstView.AVATAR_FOLDER_PATH_FOR_JAVAFX + avatarImage, 60,60, false, true);
            // TODO not the best way to create an image...
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.chatRoom = chatRoom;
        this.x = x;
        this.y = y;
    }

    public Image getAvatar() {
        return avatar;
    }
    public String getNickname() {
        return nickname;
    }

    // The get and set methods below does not need to be synchronized since the x and y variables are volatile = thread safe
    public int getChatRoom() {
        return chatRoom;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setChatRoom(int chatRoom) {
        this.chatRoom = chatRoom;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
}
