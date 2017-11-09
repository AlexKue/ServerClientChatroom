package chatroom.client;

import chatroom.model.message.LoginMessage;
import chatroom.model.message.PublicTextMessage;
import chatroom.serializer.Serializer;

import java.io.OutputStream;
import java.util.Scanner;

/**
 * Handles sending messages by deserializing them properly based on
 * type of message
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
                PublicTextMessage message = new PublicTextMessage(stringMessage, client.getLoginName());
                serializer.serialize(out, message);
            }
        }
    }
    public void authenticate() {
        System.out.print("Enter your login name: ");
        String loginName = sc.nextLine();
        client.setLoginName(loginName);
        System.out.print("Enter your password: ");
        String password = sc.nextLine();
        serializer.serialize(out, new LoginMessage(loginName, password));
    }

}
