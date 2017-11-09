package chatroom.model.message;

public class PublicServerMessage extends Message {
    private String message;
    public PublicServerMessage(String message) {
        type = 0;
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
