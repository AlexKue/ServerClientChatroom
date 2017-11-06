package chatroom.client;

import chatroom.model.*;
import chatroom.serializer.Serializer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;

import java.io.IOException;
import java.io.InputStream;

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
                display(m, type);

            } catch (IOException ex) {
                System.err.println("Connection Lost!");
                ex.printStackTrace();
                isRunning = false;
            }
        }
    }

    private void display(Message message, byte type) {
        switch (client.getMessageTypeDictionary().getType(type)) {
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
