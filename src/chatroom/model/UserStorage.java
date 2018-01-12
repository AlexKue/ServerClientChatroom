package chatroom.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;

/**
 * This contains UserData like passwords, loginNames and DisplayNames of all users registered on the server.
 */
public class UserStorage {

    String filenameUserData = "userData.ser";
    String filenameBannList = "bannList.ser";
    private ArrayList<UserAccountInfo> userInfo; //The list of all the Data of Users
    private ArrayList<UserAccountInfo> banList; //list of users being banned

    public UserStorage() {
        userInfo = readUserDataFromFile();
        banList = readBlackListFromFile();
    }

    /**
     * Registers a new UserConnectionInfo to the server, saving the loginName and the password
     *
     * @param loginName The loginName the UserConnectionInfo wants to register with
     * @param password  The password the UserConnectionInfo wishes to use
     */
    public void addClient(String loginName, String password) {
        userInfo.add(new UserAccountInfo(loginName, password));
    }

    /**
     * Checks if the user enters the correct loginName and the password correctly
     *
     * @param loginName The loginName the user wants to register with
     * @param password  The password the user wants to login with
     * @return true if the password of its corresponding loginName was correct, false otherwise
     */
    public boolean checkLogin(String loginName, String password) {
        for (UserAccountInfo u : userInfo) {
            if (u.getLoginName().equals(loginName) && u.isPasswordCorrect(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * retuns the UserAccountInfo of an User
     *
     * @param loginName the name the user needs to use to log into its account
     * @return the UserAccountInfo
     */
    public UserAccountInfo getUserAccountInfo(String loginName) {
        for (UserAccountInfo u : userInfo) {
            if (u.getLoginName().equals(loginName)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Checks in the Storage if the loginName is already in use on this server
     *
     * @param loginName the loginName is question
     * @return true if the loginName exists on this server, false otherwise
     */
    public boolean loginNameExists(String loginName) {
        for (UserAccountInfo u : userInfo) {
            if (u.getLoginName().equals(loginName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the displayName of the logged in user
     *
     * @param loginName the loginName of an User
     * @return the name displayed for other clients
     */
    public Optional<String> getDisplayName(String loginName) {
        for (UserAccountInfo u : userInfo) {
            if (u.getLoginName().equals(loginName)) {
                return Optional.of(u.getDisplayName());
            }
        }
        return Optional.empty();
    }

    /**
     * Sets the name which should be visible for other clients
     *
     * @param loginName   the loginName of the User
     * @param displayName the new displayName for the corresponding loginName
     */
    public void setDisplayName(String loginName, String displayName) {
        for (UserAccountInfo u : userInfo) {
            if (u.getLoginName().equals(loginName)) {
                u.setDisplayName(displayName);
            }
        }
    }

    public void saveUserDataToFile() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filenameUserData));
            out.writeObject(userInfo);
            out.close();
        } catch (Exception ignored) {
        }
    }

    private ArrayList<UserAccountInfo> readUserDataFromFile() {
        // read the object from file
        // save the object to file

        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(filenameUserData);
            in = new ObjectInputStream(fis);
            userInfo = (ArrayList<UserAccountInfo>) in.readObject();
            in.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
            return new ArrayList<>();
        }
        System.out.println(userInfo);
        return userInfo;
    }

    public void saveBannListToFile() {
        // save the object to file
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filenameBannList));
            out.writeObject(banList);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<UserAccountInfo> readBlackListFromFile() {
        // read the object from file

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filenameBannList));
            banList = (ArrayList<UserAccountInfo>) in.readObject();
            in.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
            return new ArrayList<>();
        }
        System.out.println(banList);
        return banList;
    }
}
