package chatroom.client;

import chatroom.client.gui.Bridge;
import chatroom.model.message.MessageTypeDictionary;
import chatroom.model.message.RoomMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    private MessageTypeDictionary messageTypeDictionary;
    private Scanner sc;
    private String loginName;
    private ClientListeningThread clientListener;
    private ClientSendingThread clientSender;
    private boolean isRunning;
    private boolean isLoggedIn;
    private String activeRoom;
    private List<String> roomUserList;
    private List<String> serverUserList;
    private List<RoomMessage> roomMessageList;
    private Bridge bridge;


    public Client() {
        messageTypeDictionary = new MessageTypeDictionary();
        sc = new Scanner(System.in);
        isRunning = true;
        setLoggedIn(false);
        roomUserList = new ArrayList<>();
        serverUserList = new ArrayList<>();
        roomMessageList = new ArrayList<>();
    }

    public void stop() {
        System.exit(0);
//        isLoggedIn = false;
//        isRunning = false;
    }

    private void start() {
        System.out.print("Please enter the address of the server to connect to: ");
        String address = sc.nextLine();
        ConnectToAdress(address);
    }

    public void ConnectToAdress(String address) {
        try {
            Socket server = new Socket(address, 54322);
            System.out.println("Connected to server!");

            InputStream dataIn = server.getInputStream();
            OutputStream dataOut = server.getOutputStream();

            clientListener = new ClientListeningThread(dataIn, this);
            clientSender = new ClientSendingThread(dataOut, this);

            clientListener.start();
            clientSender.start();

            activeRoom = "lobby";
            bridge.onConnectionAttemtResponse(true);
        } catch (IOException ex) {
            System.err.println("Connection Error! " + ex.toString());
            bridge.onConnectionAttemtResponse(false);
        }
    }
    public void login(String username, String password) {
        clientSender.login(username,password);
        loginName = username;
    }


    public void sendMessage(String message) {
        clientSender.sendMessage(message);
    }

//    public static void main(String args[]) {
//        Client client = new Client();
//        client.start();
//    }

    /** GETTER / SETTER / CHECKS **/

    public MessageTypeDictionary getMessageTypeDictionary() {
        return messageTypeDictionary;
    }

    public String getUsername() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public ClientListeningThread getClientListener() {
        return clientListener;
    }

    public ClientSendingThread getClientSender() {
        return clientSender;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized void setRunning(boolean running) {
        isRunning = running;
    }

    public synchronized boolean isLoggedIn() {
        return isLoggedIn;
    }

    public synchronized void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(String activeRoom) {
        this.activeRoom = activeRoom;
    }

    public ArrayList<String> getRoomUserList() {
        return (ArrayList<String>) roomUserList;
    }

    public void setRoomUserList(ArrayList<String> roomUserList) {
        this.roomUserList.clear();
        this.roomUserList.addAll(roomUserList);
    }

    public List<RoomMessage> getRoomMessageList() {
        return roomMessageList;
    }

    public void setRoomMessageList(List<RoomMessage> roomMessageList) {
        this.roomMessageList = roomMessageList;
    }

    public ArrayList<String> getRooms() {
        ArrayList<String> rooms = new ArrayList<>();
        for(RoomMessage m : roomMessageList){
            rooms.add(m.getName());
        }
        return rooms;
    }

    public List<String> getAllUsers() {
        return serverUserList;
    }

    public void setServerUserList(List<String> serverUserList){
        this.serverUserList = serverUserList;
    }

    public void requestRoomChange(String room) {
        clientSender.changeRoom(room);
    }

    public Bridge getBridge() {
        return bridge;
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }
}
