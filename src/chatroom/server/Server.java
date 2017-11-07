package chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

import chatroom.model.MessageTypeDictionary;
import chatroom.server.listener.MessageListener;
import chatroom.server.listener.NetworkListener;

public class Server {
    private ServerSocket listener;
    private NetworkListener networkListener;
    private MessageListener messageListener;
    private MessageTypeDictionary messageTypeDictionary;
    private Scanner sc;
    private boolean isRunning;

    public Server(){
        messageTypeDictionary = new MessageTypeDictionary();
        sc = new Scanner(System.in);
        isRunning = true;
    }

    public void start(){
        try {
            listener = new ServerSocket(54322);
            networkListener = new NetworkListener(this);
            messageListener = new MessageListener(this);
            System.out.println("Server Online!");
            getNetworkListener().start();
            getMessageListener().start();
        } catch (IOException ex) {
            System.err.println("Error while starting the server!");
            ex.printStackTrace();
        }

        while(isRunning()){
            if(sc.nextLine().trim().equals("!quit")){
                stop();
            }
        }
    }
    private void stop(){
        System.out.println("*** Shutting down Server... ***");
        try {
            System.out.println("- Closing the Server Socket");
            listener.close();
            getMessageListener().interrupt();
        } catch (IOException e) {
            System.err.println("*** Error while closing the server! ***");
            e.printStackTrace();
        }
        isRunning = false;
    }
    public boolean isRunning(){
        return isRunning;
    }

    public static void main (String args[]){
        Server server = new Server();
        server.start();
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
}
