package chatroom.model;

import java.io.Serializable;

public abstract class Message implements Serializable {
    protected Byte type; // helps the Serializer to (de-)serialize accordingly

    public Byte getType(){
        return type;
    }
}
