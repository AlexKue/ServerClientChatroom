package chatroom.model.message;

public class WarningMessage extends Message {

    private int severity;
    private String message;

    public WarningMessage(int severity, String message){
        this.severity = severity;
        this.message = message;
        type = 10;
    }

    public int getSeverity(){
        return severity;
    }

    public String getMessage() {
        return message;
    }
}
