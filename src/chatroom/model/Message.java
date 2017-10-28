package chatroom.model;

public abstract class Message {//a byte defines the type of message for the serializer to
    Byte type;
    //TODO
    public Byte getType(){
        return type;
    }
}
