package chatroom.client;

import chatroom.model.LogoutMessage;
import chatroom.model.PublicTextMessage;
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
        //run authentificationprocess
        authentificate();
        while (client.isLoggedIn()) {
            //read input of System.in
            String stringMessage = sc.nextLine();

            if (stringMessage.trim().equals("!quit")) {
                LogoutMessage l = new LogoutMessage();
                //TODO: implement serialize of logout
                //serializer.serialize(out, );
                client.stop();
            }

            PublicTextMessage message = new PublicTextMessage(stringMessage, client.getName());
            serializer.serialize(out, (byte) 1, message);
        }
    }

    private void authentificate() {
        while(!client.isLoggedIn()){
            //TODO: authentification process

        }
    }

}
