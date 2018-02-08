package chatroom.server.room;

import chatroom.model.UserConnectionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A Room contains the list of users who are currently in it. A room will always be in the list of the RoomHandler.
 */

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

    /**
     * Returns a List of UserConnectionInfos of users in the Room
     * @return a List of UserConnectionInfos of users in the room
     */
    public List<UserConnectionInfo> getUserList(){
        return userList;
    }

    /**
     * Returns a List of Strings containing the usernames of users in the room
     * @return a List of String of usernames in the room
     */
    public List<String> getUserNameList(){
        List<String> userNameList = new ArrayList<>();
        for(UserConnectionInfo u : userList){
            userNameList.add(u.getUserAccountInfo().getLoginName());
        }
        return userNameList;
    }
}
