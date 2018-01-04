package chatroom.model.message;

public class RoomMessage extends Message{
    private String name;
    private int size;

    public RoomMessage(String name, int size){
        this.name = name;
        this.size = size;
    }

    public String getName(){
        return name;
    }

    public int getSize(){
        return size;
    }
}
