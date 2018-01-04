package chatroom.client;

import chatroom.model.message.LoginMessage;
import chatroom.model.message.LogoutMessage;
import chatroom.model.message.Message;
import chatroom.model.message.PublicTextMessage;
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
        authenticate();
        while (client.isRunning()) {
            if (client.isLoggedIn()) {

                //read input of System.in
                String stringMessage = sc.nextLine();
                sendMessage(stringMessage);
            }
        }
        System.out.println("Shutting down sending handler!");
        client.stop();
    }

    /**
     * Checks the String for commands and serializes the message
     * @param m the Message
     */
    private void sendMessage(String m){
        Message message;
        //Check, if the line was a command
        if (m.trim().equals("!quit")) {
            System.out.println("You are now logging out!\nShutting down Client!");
            client.setLoggedIn(false);
            client.setRunning(false);
            message = new LogoutMessage();
        } else {
            //handle input as normal TextMessage
            message = new PublicTextMessage(m, client.getLoginName());
        }
        try {
            serializer.serialize(out, message);
        } catch (IOException e) {
            System.err.println("Error while serializing!");
        }
    }

    /**
     * Asks the User to enter his loginName and his password, and sends a LoginMessage to the server.
     */
    public void authenticate() {
        System.out.print("Enter your login name: ");
        String loginName = sc.nextLine();
        client.setLoginName(loginName);
        System.out.print("Enter your password: ");
        String password = sc.nextLine();
        try {
            serializer.serialize(out, new LoginMessage(loginName, password));
        } catch (IOException e) {
            System.err.println("Error while serializing!");
        }
    }

}
