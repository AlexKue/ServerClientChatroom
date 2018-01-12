package chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import chatroom.model.UserConnectionInfo;
import chatroom.model.message.MessageTypeDictionary;
import chatroom.model.message.WarningMessage;
import chatroom.server.gui.Bridge;
import chatroom.server.listener.MessageListener;
import chatroom.server.listener.NetworkListener;
import chatroom.server.listener.UserListeningThread;
import chatroom.server.room.Room;
import chatroom.server.room.RoomHandler;

public class Server {
    public static final Logger logger = Logger.getAnonymousLogger();
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

    private void start(){
        try {
            // Open new Socket
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
            logger.log(Level.SEVERE, "Failed to start server!",ex);
            System.exit(1);
        }

//        while(isRunning()){
//            if(sc.nextLine().trim().equals("!quit")){
//                stop();
//            }
//        }
    }
    private void stop(){
        logger.log(Level.WARNING, "Closing the Server Socket!");
        try {
            listener.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception was thrown: ", e);
        } finally {
            isRunning = false;
        }
    }

    public static void main (String args[]){
        Server server = new Server();
        server.start();
    }
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

    public void kickUser(String user) {
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)){
                WarningMessage m = new WarningMessage(1,"You have been kicked from the Server.");
                m.setUserConnectionInfo(u.getUserConnectionInfo());
                try {
                    messageListener.getMessageQueue().put(m);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void warnUser(String user) {
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)){
                WarningMessage m = new WarningMessage(0,"You received a Warning! Please don't disrupt the server!");
                m.setUserConnectionInfo(u.getUserConnectionInfo());
                try {
                    messageListener.getMessageQueue().put(m);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void banUser(String user) {
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn() && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(user)){
                WarningMessage m = new WarningMessage(2,"You have been banned from the Server.");
                m.setUserConnectionInfo(u.getUserConnectionInfo());
                try {
                    messageListener.getMessageQueue().put(m);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void editRoom(String oldName, String newName) {
        roomHandler.editRoom(oldName,newName);
    }

    public void addRoom(String name) {
        roomHandler.addRoom(name);
    }

    public void deleteRoom(String name) {
        roomHandler.removeRoom(name);
    }

    public ArrayList<String> getAllUsers() {
        ArrayList <String> userNames = new ArrayList<>();

        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn()){
                userNames.add(u.getUserConnectionInfo().getUserAccountInfo().getLoginName());
            }
        }
        return userNames;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }
}
