package chatroom.model.message;

public class TargetedTextMessage extends UserMessage {
    private String message;
    private String receiver;

    public TargetedTextMessage(String message, String sender, String receiver){
        this.message = message;
        type = 2;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }
}
