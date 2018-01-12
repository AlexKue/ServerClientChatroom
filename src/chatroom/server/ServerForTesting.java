package chatroom.server;

import chatroom.server.gui.Bridge;

import java.util.ArrayList;

public class ServerForTesting {
    Bridge bridge;
    ArrayList<String> rooms = new ArrayList<String>(){{
        add("Slim");
        add("shady");
        add("lol");
    }};
    ArrayList<String> users = new ArrayList<String>(){{
        add("boys");
        add("thefox");
        add("girlz");
    }};

    public void setBridge(Bridge bridge){
        this.bridge = bridge;
    }

    public ArrayList<String> requestRoomList() {
        return rooms;
    }

    public ArrayList<String> requestUserList() {
        return users;
    }

    public void kickUser(String user) {
        for(int i = 0; i<users.size(); i++){
            if(users.get(i).equals(user)){
                users.remove(i);
                bridge.updateUserListView(users);
            }
        }
    }

    public void warnUser(String user) {

    }

    public void banUser(String user) {
        for(int i = 0; i<users.size(); i++){
            if(users.get(i).equals(user)){
                users.remove(i);
                bridge.updateUserListView(users);
            }
        }
    }

    public void editRoom(String oldName, String newName) {
        for(int i = 0; i<rooms.size(); i++){
            if(rooms.get(i).equals(oldName)){
                rooms.set(i, newName);
                bridge.updateRoomListView(rooms);
            }
        }
    }

    public  void addRoom(String name){
        rooms.add(name);
        bridge.updateRoomListView(rooms);
    }

    public void deleteRoom(String name) {
        for(int i = 0; i<rooms.size(); i++){
            if(rooms.get(i).equals(name)){
                rooms.remove(i);
                bridge.updateRoomListView(rooms);
            }
        }

    }
}
