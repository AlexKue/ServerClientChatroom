package chatroom.model.message;


import java.util.List;

public class RoomListMessage extends Message {
    private List<RoomMessage> roomList;

    public RoomListMessage(List roomList){
        this.roomList = roomList;
        type = 7;
    }

    public List<RoomMessage> getRoomList(){
        return roomList;
    }
}
