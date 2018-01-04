package chatroom.model.message;

public class TargetedServerMessage extends Message implements Displayable {
    private String message;

    public TargetedServerMessage(String message){
        type = 5;
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
