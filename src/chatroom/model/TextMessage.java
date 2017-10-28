package chatroom.model;

public class TextMessage extends Message{
    private String message;
    private final Byte type= 0; //A Byte is sent to the server so that the serializer handles the message accordingly

    public TextMessage(String message){
        this.message = message;
    }
}


