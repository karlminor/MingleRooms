package Client;

import javafx.scene.image.Image;

public class User {
    private int id;
    private String nickname;
    private String avatarImagePath;
    private Image avatar;
    private volatile int x;
    private volatile int y;

    public User(int id, String nickname, String avatarImagePath, int x, int y) {
        this.id = id;
        this.nickname = nickname;
        this.avatarImagePath = avatarImagePath;
        try {
            avatar = new Image(avatarImagePath, 60,60, false, true);
            // TODO not the best way to create an image...
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
