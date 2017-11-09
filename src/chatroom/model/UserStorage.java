package chatroom.model;

import java.util.ArrayList;
import java.util.HashMap;

public class UserStorage {
    ArrayList<UserAccountInfo> userInfo;
    public UserStorage(){
        userInfo = new ArrayList<>();
    }

    /**
     * Registres a new UserConnectionInfo to the server, saving the loginname and the password
     * @param loginname The loginname the UserConnectionInfo wants to register with
     * @param password The password the UserConnectionInfo wishes to use
     */
    public void addClient(String loginname, String password){
        userInfo.add(new UserAccountInfo(loginname, password));
    }

    /**
     * Checks if the user enters the correct loginname and the password correctly
     * @param loginname The loginname the user wants to register with
     * @param password The password the user wants to login with
     * @return
     */
    public boolean checkLogin(String loginname, String password){
        for (UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginname) && u.isPasswordCorrect(password)){
                return true;
            }
        }
        return false;
    }
    public UserAccountInfo getUserAccountInfo(String loginName){
        for(UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginName)){
                return u;
            }
        }
        return null;
    }

    /**
     * Checks in the Storage if the user is registred on this server
     * @param loginname
     * @return true if the loginname exists on this server, false otherwise
     */
    public boolean loginNameExists(String loginname){
        for(UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginname)){
                return true;
            }
        }
        return false;
    }

    /**
     * Return the displayname of the logged in user
     * @param loginname
     * @return the name displayed for other clients
     */
    public String getDisplayname(String loginname){
        for (UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginname)){
                return u.getDisplayName();
            }
        }
        return null;
    }

    /**
     * Sets the name which should be visible for other clients
     * @param loginname
     * @param displayname
     */
    public void setDisplayname(String loginname, String displayname){
        for (UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(u.getLoginName())){
                u.setDisplayName(displayname);
            }
        }
    }
}
