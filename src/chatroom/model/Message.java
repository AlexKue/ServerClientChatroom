package chatroom.model;

public abstract class Message {
    protected byte type;

    public byte getType(){
        return type;
    }
}
