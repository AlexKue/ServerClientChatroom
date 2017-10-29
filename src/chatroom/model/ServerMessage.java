package chatroom.model;

public class ServerMessage extends Message {
    private String message;
    public ServerMessage(String message) {
        type = 0;
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
