package chatroom.model.message;

import java.util.List;

public class ServerUserListMessage extends Message{
    private List<String> serverUserList;

    public ServerUserListMessage(List<String> serverUserList){
        this.serverUserList = serverUserList;
        type = 11;
    }

    public List<String> getServerUserList() {
        return serverUserList;
    }
}
