package chatroom.model;

public class AcceptedMessage extends Message {
    private boolean accepted;
    public AcceptedMessage(boolean accepted){
        this.accepted = accepted;
    }
    public boolean getAcceptanceStatus(){
        return accepted;
    }
}
