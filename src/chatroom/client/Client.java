package chatroom.client;

import chatroom.model.message.MessageTypeDictionary;
import chatroom.model.message.RoomMessage;

import java.io.*;
import java.net.Socket;
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
    private List<String> userList;
    private List<RoomMessage> roomMessageList;


    private Client() {
        messageTypeDictionary = new MessageTypeDictionary();
        sc = new Scanner(System.in);
        isRunning = true;
        setLoggedIn(false);
    }

    public void stop() {
        System.exit(0);
//        isLoggedIn = false;
//        isRunning = false;
    }

    private void start() {
        System.out.print("Please enter the address of the server to connect to: ");
        String address = sc.nextLine();
        connect(address);
    }

    public void connect(String address) {
        try {
            Socket server = new Socket(address, 54322);
            System.out.println("Connected to server!");

            InputStream dataIn = server.getInputStream();
            OutputStream dataOut = server.getOutputStream();

            clientListener = new ClientListeningThread(dataIn, this);
            clientSender = new ClientSendingThread(dataOut, this);

            clientListener.start();
            clientSender.start();
        } catch (IOException ex) {
            System.err.println("Connection Error! " + ex.toString());
        }
    }

    public static void main(String args[]) {

        Client client = new Client();
        client.start();

    }

    public MessageTypeDictionary getMessageTypeDictionary() {
        return messageTypeDictionary;
    }

    public String getLoginName() {
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

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public List<RoomMessage> getRoomMessageList() {
        return roomMessageList;
    }

    public void setRoomMessageList(List<RoomMessage> roomMessageList) {
        this.roomMessageList = roomMessageList;
    }
}
