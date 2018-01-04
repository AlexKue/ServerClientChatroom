package chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

import chatroom.model.message.MessageTypeDictionary;
import chatroom.server.listener.MessageListener;
import chatroom.server.listener.NetworkListener;
import chatroom.server.room.RoomHandler;

public class Server {
    private ServerSocket listener;
    private NetworkListener networkListener;
    private MessageListener messageListener;
    private MessageTypeDictionary messageTypeDictionary;
    private Scanner sc;
    private boolean isRunning;
    private RoomHandler roomHandler;

    private Server(){
        messageTypeDictionary = new MessageTypeDictionary();
        sc = new Scanner(System.in);
        isRunning = true;
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
            roomHandler = new RoomHandler();

            System.out.println("*** Server Online! ***");
        } catch (IOException ex) {
            System.err.println("*** Error while starting the server! ***");
            ex.printStackTrace();
            System.exit(1);
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
            System.out.println("*** Closing the Server Socket ***");
            listener.close();
        } catch (IOException e) {
            System.err.println("*** Error while closing the server! ***");
            e.printStackTrace();
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
}
