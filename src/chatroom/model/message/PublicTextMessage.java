package chatroom.model.message;

public class PublicTextMessage extends UserMessage{
    private String message;

    public PublicTextMessage(String message, String sender){
        this.message = message;
        type = 1;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }
}


