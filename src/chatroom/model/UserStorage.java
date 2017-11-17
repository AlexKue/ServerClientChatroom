package chatroom.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This contains UserData like passwords, loginNames and DisplayNames of all users registered on the server.
 */
public class UserStorage {
    private ArrayList<UserAccountInfo> userInfo; //The list of all the Data of Users

    public UserStorage(){
        userInfo = new ArrayList<>();
    }

    /**
     * Registers a new UserConnectionInfo to the server, saving the loginName and the password
     * @param loginName The loginName the UserConnectionInfo wants to register with
     * @param password The password the UserConnectionInfo wishes to use
     */
    public void addClient(String loginName, String password){
        userInfo.add(new UserAccountInfo(loginName, password));
    }

    /**
     * Checks if the user enters the correct loginName and the password correctly
     * @param loginName The loginName the user wants to register with
     * @param password The password the user wants to login with
     * @return true if the password of its corresponding loginName was correct, false otherwise
     */
    public boolean checkLogin(String loginName, String password){
        for (UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginName) && u.isPasswordCorrect(password)){
                return true;
            }
        }
        return false;
    }

    /**
     * retuns the UserAccountInfo of an User
     * @param loginName the name the user needs to use to log into its account
     * @return the UserAccountInfo
     */
    public UserAccountInfo getUserAccountInfo(String loginName){
        for(UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginName)){
                return u;
            }
        }
        return null;
    }

    /**
     * Checks in the Storage if the loginName is already in use on this server
     * @param loginName the loginName is question
     * @return true if the loginName exists on this server, false otherwise
     */
    public boolean loginNameExists(String loginName){
        for(UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginName)){
                return true;
            }
        }
        return false;
    }

    /**
     * Return the displayName of the logged in user
     * @param loginName the loginName of an User
     * @return the name displayed for other clients
     */
    public String getDisplayName(String loginName){
        for (UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginName)){
                return u.getDisplayName();
            }
        }
        return null;
    }

    /**
     * Sets the name which should be visible for other clients
     * @param loginName the loginName of the User
     * @param displayName the new displayName for the corresponding loginName
     */
    public void getDisplayName(String loginName, String displayName){
        for (UserAccountInfo u : userInfo){
            if(u.getLoginName().equals(loginName)){
                u.setDisplayName(displayName);
            }
        }
    }
}
