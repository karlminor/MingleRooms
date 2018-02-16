package Server.MessageLogic;

import Server.UserLogic.User;

public class Message {
    public final String text;
    public final User sender;

    public Message(User sender, String text){
        this.sender = sender;
        this.text = text;
    }
}
