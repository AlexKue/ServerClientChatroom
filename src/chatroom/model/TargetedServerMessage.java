package chatroom.model;

public class TargetedServerMessage extends Message {
    private int id;
    private String message;

    public TargetedServerMessage(int id, String message){
        this.id = id;
        this.message = message;
    }
    public int getId(){
        return id;
    }
    public String getMessage(){
        return message;
    }
}
