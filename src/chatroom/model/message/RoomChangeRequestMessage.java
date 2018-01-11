package chatroom.model.message;

public class RoomChangeRequestMessage extends Message {

    private String roomName;
    private String loginName;

    public RoomChangeRequestMessage(String roomName, String loginName){
        this.roomName = roomName;
        this.loginName = loginName;
        type = 9;

    }

    public String getRoomName() {
        return roomName;
    }

    public String getLoginName() {
        return loginName;
    }
}
