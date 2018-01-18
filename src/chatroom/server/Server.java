package chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import chatroom.model.UserAccountInfo;
import chatroom.model.UserConnectionInfo;
import chatroom.model.message.MessageTypeDictionary;
import chatroom.model.message.RoomNameEditMessage;
import chatroom.model.message.WarningMessage;
import chatroom.server.gui.Bridge;
import chatroom.server.listener.MessageListener;
import chatroom.server.listener.NetworkListener;
import chatroom.server.listener.UserListeningThread;
import chatroom.server.room.Room;
import chatroom.server.room.RoomHandler;

public class Server {
    public static final Logger logger = Logger.getLogger("Server");
    private FileHandler fh;
    private ServerSocket listener;
    private NetworkListener networkListener;
    private MessageListener messageListener;
    private MessageTypeDictionary messageTypeDictionary;
    private Scanner sc;
    private boolean isRunning;
    private RoomHandler roomHandler;
    private Bridge bridge;

    public Server(){
        messageTypeDictionary = new MessageTypeDictionary();
        sc = new Scanner(System.in);
        isRunning = true;
        start();
    }

    /**
     * Start all Threads and handlers
     */
    private void start(){
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
            logger.log(Level.INFO,"Server Online!");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Server: Failed to start server!",ex);
            System.exit(1);
        }
    }



    public void stop(){
        log(Level.WARNING,"Server: Closing the Server Socket!");
        try {
            listener.close();
        } catch (IOException e) {
            log(Level.SEVERE,"Server: Exception was thrown: " + e.toString());
        } finally {
            isRunning = false;
        }
    }

    public static void main (String args[]){
        Server server = new Server();
        server.start();
    }


    public ArrayList<String> requestRoomList() {
        ArrayList<String> roomNames = new ArrayList<>();

        for(Room r : roomHandler.getRoomList()){
            roomNames.add(r.getName());
        }
        return roomNames;
    }

    public ArrayList<String> requestUserList() {
        ArrayList<String> userNames = new ArrayList<>();
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn()){
                userNames.add(u.getUserConnectionInfo().getUserAccountInfo().getLoginName());
            }
        }
        return userNames;
    }

    /**
     * Sends a Message to the user and kicks him.
     * @param user
     */
    public void kickUser(String user) {
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)){
                WarningMessage m = new WarningMessage(1,"You have been kicked from the Server.");
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
        bridge.updateUserListView(requestUserList());
    }

    /**
     * Just sends a text message to the user warning him.
     * @param user
     */
    public void warnUser(String user) {
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)){
                WarningMessage m = new WarningMessage(0,"You received a Warning! Further disturbance may have consequences!!");
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
        bridge.updateUserListView(requestUserList());
    }

    /**
     * Kicks then User from the Server and puts him onto the banlist, preventing him to reconnect using that username
     * @param user
     */
    public void banUser(String user) {
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)){
                WarningMessage m = new WarningMessage(2,"You have been banned from the Server.");
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
        bridge.updateUserListView(requestUserList());
    }

    public void editRoom(String oldName, String newName) {
        if(roomHandler.editRoom(oldName,newName)) try {
            messageListener.getMessageQueue().put(new RoomNameEditMessage(newName));
        } catch (InterruptedException e) {
            log(Level.SEVERE,"Roomhandler: Exception while notifying users of the new Name " + oldName,e);
        }
    }

    public void addRoom(String name) {
        roomHandler.addRoom(name);
    }

    public void deleteRoom(String name) {
        roomHandler.removeRoom(name);
    }

    public ArrayList<String> getAllUsers() {
        ArrayList <String> userNames = new ArrayList<>();
        for(UserAccountInfo u : messageListener.getUserStorage().getUserInfoList()){
            userNames.add(u.getLoginName());
        }
        System.out.println(userNames);
        return userNames;
    }
    public void log(Level level, String msg, Exception ex) {
        logger.log(level, msg, ex);
        bridge.addEventToLog(msg + ex.toString());
    }
    public void log(Level level, String msg){
        logger.log(level,msg);
        bridge.addEventToLog(msg);
    }


    /** Setter/Getter  */
    public boolean isRunning(){
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

    public RoomHandler getRoomHandler(){
        return roomHandler;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }
}
