package chatroom.client;

import chatroom.model.*;
import chatroom.serializer.Serializer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles incoming messages by deserializing the data and putting them in a Message Object,
 * displaying them afterwards
 */
public class ClientListeningThread extends Thread {
    private Client client;
    private InputStream in;
    private Serializer serializer;

    public ClientListeningThread(InputStream in, Client client) throws IOException {
        this.in = in;
        serializer = new Serializer();
        this.client = client;
    }

    @Override
    public void run() {
        boolean isRunning = true;
        while (isRunning) {
            try {
                byte type = (byte)in.read();
                Message m = serializer.deserialize(in, type);
                display(m);

            } catch (IOException ex) {
                System.err.println("Connection Lost!");
                ex.printStackTrace();
                isRunning = false;
            }
        }
    }

    /**
     * Prints a String, created depending on which type of Message you want to print.
     * @param message The message which should be transformed into a String to be displayed
     */
    private void display(Message message) {
        switch (client.getMessageTypeDictionary().getType(message.getType())) {
            case PUBLICTEXTMSG:
                PublicTextMessage publicTextMessage = ((PublicTextMessage) message);
                String publicString = publicTextMessage.getSender() + ": " + publicTextMessage.getMessage();
                System.out.println(publicString);
                break;
            case SERVERMSG:
                ServerMessage serverMessage = ((ServerMessage) message);
                System.out.println("*** " + serverMessage.getMessage() + " ***");
                break;
            case TARGETTEXTMSG:
                TargetedTextMessage targetedTextMessage = ((TargetedTextMessage) message);
                String targetedString = targetedTextMessage.getSender() + " (whispered): " + targetedTextMessage.getMessage();
                System.out.println(targetedString);
        }

    }

}
