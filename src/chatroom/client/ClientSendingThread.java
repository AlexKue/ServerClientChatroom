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
     * Serializes the Message as a PublicTextMessage
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


    /**
     * Serializes the Message as a TargetedTextMessage, used in Private Chats
     * @param m the message
     * @param receiver the name of the receiving Client
     */
    public void sendMessage(String m, String receiver) {
        Message message = new TargetedTextMessage(m, client.getUsername(),receiver);
        try {
            serializer.serialize(out, message);
        } catch (IOException e) {
            System.err.println("Error while serializing!");
        }
    }

    /**
     * Serializes the name and pasword as a LoginMessage
     * @param loginName
     * @param password
     */
    public void login(String loginName, String password){
        try {
            serializer.serialize(out, new LoginMessage(loginName, password));
        } catch (IOException e) {
            System.err.println("Error while serializing!");
        }
    }

    /**
     * Sends a RoomChangeRequest to the server, in which the server will respond with a RoomChangeResponse
     * @param roomName
     */
    public void changeRoom(String roomName){
        try {
            serializer.serialize(this.out, new RoomChangeRequestMessage(roomName,client.getUsername()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a Request to the server to start a private Chat with a Client. The server will forward the request to
     * the Client who should be texted
     * @param targetUser the name of the User who should participate in the private Chat
     */
    public void startPrivateChat(String targetUser){
        Message message = new PrivateChatStartRequestMessage(client.getUsername(),targetUser);
        try {
            serializer.serialize(this.out,message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a Request to the server to end a private Chat with a Client. The server will forward the request to the
     * Client who should receive the request
     * @param endingUser
     * @param userToBeInformed
     */
    public void endPrivateChat(String endingUser, String userToBeInformed) {
        Message message = new PrivateChatEndRequestMessage(endingUser, userToBeInformed);
        try {
            serializer.serialize(this.out, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
