package chatroom.model.message;

import chatroom.model.UserConnectionInfo;

public abstract class Message {
    protected byte type;
    private transient UserConnectionInfo connectionInfo;

    public byte getType(){
        return type;
    }

    public UserConnectionInfo getUserConnectionInfo() {
        return connectionInfo;
    }

    public void setUserConnectionInfo(UserConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
}
