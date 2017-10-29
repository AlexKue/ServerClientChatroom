package chatroom.model;

public abstract class Message {
    protected Byte type; // helps the Serializer to (de-)serialize accordingly

    public Byte getType(){
        return type;
    }
}
