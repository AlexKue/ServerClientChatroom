package chatroom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import chatroom.model.message.MessageTypeDictionary;
import chatroom.server.listener.MessageListener;
import chatroom.server.listener.NetworkListener;
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

            logger.log(Level.INFO,"Server Online!");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to start server!",ex);
            System.exit(1);
        }

        while(isRunning()){
            if(sc.nextLine().trim().equals("!quit")){
                stop();
            }
        }
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
}
