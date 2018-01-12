package chatroom.server.room;

import java.util.ArrayList;
import java.util.List;

public class RoomHandler {
    private List<Room> roomList;

    public RoomHandler() {
        roomList = new ArrayList<>();
        roomList.add(new Room("lobby"));
    }

    public void addRoom(String name) {
        roomList.add(new Room(name));
    }

    public void removeRoom(Room room) {
        roomList.remove(room);
    }

    public void removeRoom(String name){
        for(Room r : roomList){
            if(r.getName().equals(name)){
                roomList.remove(r);
                break;
            }
        }
    }

    public void editRoom() {
        //TODO
    }

    public Room getRoom(String name){
        for(Room r : roomList){
            if(r.getName().equals(name)){
                return r;
            }
        }
        return null;
    }

    public List<Room> getRoomList(){
        return roomList;
    }
}
