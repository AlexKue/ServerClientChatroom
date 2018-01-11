package chatroom.model.message;

import java.util.List;

public class UserListMessage extends Message{
    private List<String> userList;

    public UserListMessage(List<String> userList){
        this.userList = userList;
        type = 8;
    }

    public List<String> getUserList(){
        return userList;
    }
}
