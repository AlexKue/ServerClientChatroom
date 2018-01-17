package chatroom.model.message;

public class RoomNameEditMessage extends Message {
    private String newName;

    public RoomNameEditMessage(String newName){
        this.newName = newName;
        type = 13;
    }

    public String getNewName() {
        return newName;
    }
}
