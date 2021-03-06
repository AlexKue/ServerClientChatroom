package chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.*;

import chatroom.model.UserAccountInfo;
import chatroom.model.UserConnectionInfo;
import chatroom.model.message.*;
import chatroom.server.gui.Bridge;
import chatroom.server.listener.MessageListener;
import chatroom.server.listener.NetworkListener;
import chatroom.server.listener.UserListeningThread;
import chatroom.server.room.Room;
import chatroom.server.room.RoomHandler;

public class Server {
    public static final Logger logger = Logger.getLogger(Server.class.getName());
    private FileHandler fh;
    private ServerSocket listener;
    private NetworkListener networkListener;
    private MessageListener messageListener;
    private MessageTypeDictionary messageTypeDictionary;
    private Scanner sc;
    private boolean isRunning;
    private RoomHandler roomHandler;
    private Bridge bridge;

    public Server() {
        messageTypeDictionary = new MessageTypeDictionary();
        sc = new Scanner(System.in);
        isRunning = true;
        start();
    }

    /**
     * Start all Threads and handlers
     */
    private void start() {
        try {
            //Create FileHandler for logging
            fh = new FileHandler("ServerLog.txt");
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fh.setFormatter(simpleFormatter);
            logger.addHandler(fh);
            listener = new ServerSocket(54322);

            //Start listening Threads
            networkListener = new NetworkListener(this);
            messageListener = new MessageListener(this);
            getNetworkListener().start();
            getMessageListener().start();

            //Create roomHandler with default room
            roomHandler = new RoomHandler(this);
            logger.log(Level.INFO, "Server Online!");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Server: Failed to start server!", ex);
            System.exit(1);
        }
    }

    public void stop() {
        log(Level.WARNING, "Server: Closing the Server Socket!");
        try {
            listener.close();
        } catch (IOException e) {
            log(Level.SEVERE, "Server: Exception was thrown: " + e.toString());
        } finally {
            isRunning = false;
        }
    }

    /**
     * Returns a list of roomNames available on the server
     *
     * @return
     */
    synchronized public ArrayList<String> requestRoomList() {
        ArrayList<String> roomNames = new ArrayList<>();

        for (Room r : roomHandler.getPublicRoomList()) {
            roomNames.add(r.getName());
        }
        return roomNames;
    }

    /**
     * Returns an ArrayList of names of the users currently logged in the server
     *
     * @return
     */
    synchronized public ArrayList<String> requestUserList() {
        ArrayList<String> userNames = new ArrayList<>();
        for (UserListeningThread u : networkListener.getUserListeningThreadList()) {
            if (u.getUserConnectionInfo().isLoggedIn()) {
                userNames.add(u.getUserConnectionInfo().getUserAccountInfo().getLoginName());
            }
        }
        return userNames;
    }

    /**
     * Sends a Message to the user and kicks him.
     *
     * @param user
     */
    synchronized public void kickUser(String user) {
        for (UserListeningThread u : networkListener.getUserListeningThreadList()) {
            if (u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)) {
                WarningMessage m = new WarningMessage(1, "You have been kicked from the Server.");
                m.setUserConnectionInfo(u.getUserConnectionInfo());
                try {
                    messageListener.getMessageQueue().put(m);
                } catch (InterruptedException e) {
                    log(Level.SEVERE, "Server: Exception while trying to kick user " + user, e);
                }
                break;
            }
            log(Level.INFO, "Server:" + user + " has been kicked from the server.");
        }
    }

    /**
     * Just sends a text message to the user warning him.
     *
     * @param user
     */
   synchronized public void warnUser(String user) {
        for (UserListeningThread u : networkListener.getUserListeningThreadList()) {
            if (u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)) {
                WarningMessage m = new WarningMessage(0, "You received a Warning! Further disturbance may have consequences!!");
                m.setUserConnectionInfo(u.getUserConnectionInfo());
                try {
                    messageListener.getMessageQueue().put(m);
                } catch (InterruptedException e) {
                    log(Level.SEVERE, "Server: Exception while trying to warn user " + user, e);
                }
                break;
            }
            log(Level.INFO, "Server: A Warning has been sent to " + user);
        }
    }

    /**
     * Kicks then User from the Server and puts him onto the banlist, preventing him to reconnect using that username
     *
     * @param user
     */
    synchronized public void banUser(String user) {
        for (UserListeningThread u : networkListener.getUserListeningThreadList()) {
            if (u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)) {
                WarningMessage m = new WarningMessage(2, "You have been banned from the Server.");
                m.setUserConnectionInfo(u.getUserConnectionInfo());
                try {
                    messageListener.getMessageQueue().put(m);
                } catch (InterruptedException e) {
                    log(Level.SEVERE, "Server: Exception while trying to ban user " + user, e);
                }
                break;
            }
            log(Level.INFO, "Server:" + user + " has been banned from the server.");
        }
    }

    /**
     * Renames a room
     *
     * @param oldName
     * @param newName
     */
    synchronized public void editRoom(String oldName, String newName) {
        if (oldName.trim().equals("lobby")) {
            log(Level.WARNING, "RoomHandler: Cannot edit the lobby.");
        } else {
            if (roomHandler.editPublicRoom(oldName, newName)) try {
                messageListener.getMessageQueue().put(new RoomNameEditMessage(newName));
                bridge.updateUserListView(getUserListWithRooms());

            } catch (InterruptedException e) {
                log(Level.SEVERE, "RoomHandler: Exception while notifying users of the new Name " + oldName, e);
            }
        }
    }

    synchronized public void addRoom(String name) {
        if (name.trim().equals("")) {
            log(Level.WARNING, "RoomHandler: Room name cannot be empty.");
        } else if (roomHandler.getRoomNamesList().contains(name.trim())) {
            log(Level.WARNING, "RoomHandler: Room already exists");
        } else {
            roomHandler.addPublicRoom(name.trim());
        }
    }

    /**
     * Moves every Client to the lobby and the deletes the room
     *
     * @param name the name of the room
     */
    synchronized public void deleteRoom(String name) {
        if (name.trim().equals("lobby")) {
            log(Level.WARNING, "RoomHandler: Cannot remove the lobby.");
        } else {
            Room r = roomHandler.getPublicRoom(name);
            for (UserConnectionInfo u : roomHandler.getPublicRoom("lobby").getUserList()) {
                TargetedServerMessage targetedServerMessage = new TargetedServerMessage("Room \"" + name + "\" has been deleted and its users have been moved to the lobby.");
                targetedServerMessage.setUserConnectionInfo(u);
                try {
                    messageListener.getMessageQueue().put(targetedServerMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (UserConnectionInfo u : r.getUserList()) {
                u.setActiveRoom(roomHandler.getPublicRoom("lobby"));
                roomHandler.getPublicRoom("lobby").addUser(u);

                RoomNameEditMessage roomNameEditMessage = new RoomNameEditMessage("lobby");
                roomNameEditMessage.setUserConnectionInfo(u);

                TargetedServerMessage targetedServerMessage = new TargetedServerMessage("Room \"" + name + "\" has been deleted and you have been moved to the lobby.");
                targetedServerMessage.setUserConnectionInfo(u);

                try {
                    messageListener.getMessageQueue().put(roomNameEditMessage);
                    messageListener.getMessageQueue().put(targetedServerMessage);
                } catch (InterruptedException e) {
                    log(Level.SEVERE, "RoomHandler: Exception while notifying users of the new Name lobby", e);
                }
            }
            List<String> lobbyUserList = roomHandler.getPublicRoom("lobby").getUserNameList();
            RoomUserListMessage roomUserListMessage = new RoomUserListMessage(lobbyUserList, "lobby");
            try {
                messageListener.getMessageQueue().put(roomUserListMessage);
            } catch (InterruptedException e) {
                log(Level.SEVERE, "RoomHandler: Exception while updating RoomUserLists", e);
            }
            r.getUserList().clear();
            roomHandler.removePublicRoom(name);
            bridge.updateUserListView(getUserListWithRooms());
        }
    }


    public synchronized void log(Level level, String msg, Exception ex) {
        Date time = new Date();
        logger.log(level, msg, ex);
        bridge.addEventToLog("[" + time.toString() + "] " + msg + ex.toString());
    }

    public synchronized void log(Level level, String msg) {
        Date time = new Date();
        logger.log(level, msg);
        bridge.addEventToLog("[" + time.toString() + "] " + msg);
    }


    /**
     * Setter/Getter
     */
    public boolean isRunning() {
        return isRunning;
    }

    public ServerSocket getListener() {
        return listener;
    }

    public NetworkListener getNetworkListener() {
        return networkListener;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public MessageTypeDictionary getMessageTypeDictionary() {
        return messageTypeDictionary;
    }

    public RoomHandler getRoomHandler() {
        return roomHandler;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    /**
     * Returns a list of usernames of all users saved in the userStorage
     *
     * @return a ArrayList of Strings containing usernames
     */
    synchronized public ArrayList<String> getAllUsers() {
        ArrayList<String> userNames = new ArrayList<>();
        for (UserAccountInfo u : messageListener.getUserStorage().getUserInfoList()) {
            userNames.add(u.getLoginName());
        }
        System.out.println(userNames);
        return userNames;
    }


    /**
     * Returns a list of userNames with the name of the room they currently are appended on it in the format
     * username|[roomname]. This method is used in the serverGui to display all users and the rooms they currently are.
     *
     * @return list of usernames with their active room appended
     */
    synchronized public ArrayList<String> getUserListWithRooms() {
        ArrayList<String> userNames = new ArrayList<>();
        for (UserListeningThread u : networkListener.getUserListeningThreadList()) {
            if (u.getUserConnectionInfo().isLoggedIn()) {
                UserAccountInfo info = u.getUserConnectionInfo().getUserAccountInfo();
                userNames.add(info.getLoginName() + "|[" + u.getUserConnectionInfo().getActiveRoom().getName() + "]");
            }
        }
        return userNames;
    }
}
