package chatroom.model.message;

public class RoomChangeResponseMessage extends Message {

    private boolean success;
    private String roomName;

    public RoomChangeResponseMessage(boolean success, String roomName){
        this.success = success;
        this.roomName = roomName;
        type = 12;
    }

    public boolean isSuccessful(){
        return success;
    }

    public String getRoomName(){
        return roomName;
    }
}
