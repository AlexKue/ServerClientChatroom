package chatroom.model.message;

import java.util.List;

public class RoomUserListMessage extends Message{
    private List<String> userList;
    private String roomName;

    public RoomUserListMessage(List<String> userList, String roomName){
        this.userList = userList;
        this.roomName = roomName;
        type = 8;
    }

    public List<String> getUserList(){
        return userList;
    }
    public String getRoomName() {
        return roomName;
    }
}
