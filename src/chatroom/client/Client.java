package chatroom.client;

import chatroom.model.message.LoginMessage;
import chatroom.model.message.MessageTypeDictionary;
import chatroom.serializer.Serializer;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private MessageTypeDictionary messageTypeDictionary;
    private Scanner sc;
    private String loginName;
    private ClientListeningThread clientListener;
    private ClientSendingThread clientSender;
    private boolean isRunning;
    private boolean isLoggedIn;


    public Client(){
        messageTypeDictionary = new MessageTypeDictionary();
        sc = new Scanner(System.in);
        isRunning = true;
        isLoggedIn = false;
    }

    public void stop(){
        System.exit(0);
//        isLoggedIn = false;
//        isRunning = false;
    }

    public void start() {
        System.out.print("Please enter the address of the server to connect to: ");
        String address = sc.nextLine();

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
    public void setLoginName(String loginName){
        this.loginName = loginName;
    }

    public ClientListeningThread getClientListener() {
        return clientListener;
    }

    public ClientSendingThread getClientSender() {
        return clientSender;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
