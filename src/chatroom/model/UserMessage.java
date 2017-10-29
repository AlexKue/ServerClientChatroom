package chatroom.model;

public abstract class UserMessage extends Message {
    protected String sender;

    public String getSender(){
        return sender;
    }

}
