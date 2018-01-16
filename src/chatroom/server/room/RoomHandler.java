package chatroom.server.room;

import chatroom.model.message.RoomListMessage;
import chatroom.model.message.RoomMessage;
import chatroom.server.Server;

import java.util.ArrayList;
import java.util.List;

public class RoomHandler {
    private List<Room> roomList;
    private Server server;

    public RoomHandler(Server server) {
        roomList = new ArrayList<>();
        roomList.add(new Room("lobby"));
        this.server = server;
    }

    public void addRoom(String name) {
        roomList.add(new Room(name));
        server.getBridge().updateRoomListView(getRoomNamesList());
        try {
            server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeRoom(Room room) {
        roomList.remove(room);
        server.getBridge().updateRoomListView(getRoomNamesList());
        try {
            server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void removeRoom(String name){
        for(Room r : roomList){
            if(r.getName().equals(name)){
                roomList.remove(r);
                server.getBridge().updateRoomListView(getRoomNamesList());
                break;
            }
        }
        try {
            server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void editRoom(String oldName, String newName) {
        Room room = getRoom(oldName);
        room.setName(newName);
        server.getBridge().updateRoomListView(getRoomNamesList());
        try {
            server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public ArrayList<String> getRoomNamesList(){
        ArrayList<String> roomNames = new ArrayList<>();
        for(Room r : roomList){
            roomNames.add(r.getName());
        }
        return roomNames;
    }

    public RoomListMessage buildRoomListMessage(){
        List<RoomMessage> list = new ArrayList<>();
        for(Room r : roomList){
            list.add(new RoomMessage(r.getName(),r.getRoomSize()));
        }
        return new RoomListMessage(list);
    }
}
