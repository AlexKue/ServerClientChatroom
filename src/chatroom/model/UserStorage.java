package chatroom.model;

import java.util.ArrayList;
import java.util.HashMap;

public class UserStorage {
    HashMap<String, String[]> clients;
    public void AddClient(String loginname, String password){
        String[] data = {loginname, password};
        clients.put(loginname, data);
    }

    public boolean checkExistingUsers(String loginname){
        if (clients.get(loginname) == null) {
            return false;
        }
        return true;
    }
    public boolean checkLogin(String loginname, String password){
        if((clients.get(loginname))[1].equals(password)){
            return true;
        }
        else
        {
            return false;
        }
    }
    public String getDisplayname(String loginname){
        return (clients.get(loginname))[0];
    }
    public void setDisplayname(String loginname, String displayname){
        String [] userdata = clients.get(loginname);    //Reading the data from the hashmap
        userdata[0] = displayname;  //changin displayname
        clients.put(loginname, userdata);   // adding the edited userdata with the new displayname to the hashmap
    }

}
