package chatroom.model;

public abstract class Message {
    protected byte type;
    private transient UserConnectionInfo source;

    public byte getType(){
        return type;
    }

    public UserConnectionInfo getId() {
        return source;
    }

    public void setUserConnectionInfo(UserConnectionInfo source) {
        this.source = source;
    }
}
