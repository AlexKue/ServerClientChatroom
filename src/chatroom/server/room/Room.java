package chatroom.server.room;

import chatroom.model.UserConnectionInfo;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<UserConnectionInfo> userList;
    private String name;

    public Room(String name){
        userList = new ArrayList<>();
        this.name = name;
    }

    public void addUser(UserConnectionInfo info){
        userList.add(info);
    }

    public void removeUser(UserConnectionInfo info){
        userList.remove(info);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getRoomSize(){
        return userList.size();
    }
}
