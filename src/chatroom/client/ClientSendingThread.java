package chatroom.client;

import chatroom.model.message.*;
import chatroom.serializer.Serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Handles sending messages by deserializing them properly based on
 * type of Message
 */
public class ClientSendingThread extends Thread {
    private OutputStream out;
    private Scanner sc = new Scanner(System.in);
    private final Serializer serializer;
    private final Client client;

    public ClientSendingThread(OutputStream out, Client client) {
        this.client = client;
        this.out = out;
        serializer = new Serializer();
    }

    @Override
    public void run() {
        //authenticate();
        while (client.isRunning()) {
            if (client.isLoggedIn()) {

//                //read input of System.in
//                String stringMessage = sc.nextLine();
//                sendMessage(stringMessage);
            }
        }
        System.out.println("Shutting down sending handler!");
//        client.stop();
    }

    /**
     * Checks the String for commands and serializes the message
     * @param m the Message
     */
    public void sendMessage(String m){
        Message message = new PublicTextMessage(m, client.getUsername());
        try {
            serializer.serialize(out, message);
        } catch (IOException e) {
            System.err.println("Error while serializing!");
        }
    }


    public void sendMessage(String m, String receiver) {
        Message message = new TargetedTextMessage(m, client.getUsername(),receiver);
        try {
            serializer.serialize(out, message);
        } catch (IOException e) {
            System.err.println("Error while serializing!");
        }
    }

    public void login(String loginName, String password){
        try {
            serializer.serialize(out, new LoginMessage(loginName, password));
        } catch (IOException e) {
            System.err.println("Error while serializing!");
        }
    }

    public void changeRoom(String roomName){
        try {
            serializer.serialize(this.out, new RoomChangeRequestMessage(roomName,client.getUsername()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPrivateChat(String targetUser){
        Message message = new PrivateChatStartRequestMessage(client.getUsername(),targetUser);
        try {
            serializer.serialize(this.out,message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
