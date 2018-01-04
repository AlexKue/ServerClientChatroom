package chatroom.model.message;

public abstract class UserMessage extends Message implements Displayable {
    protected String sender;

    public String getSender(){
        return sender;
    }

}
