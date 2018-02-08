package chatroom.client;

import chatroom.model.message.*;
import chatroom.serializer.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Deserialize data from Stream and puts it in a Message Object,
 * displaying them afterwards
 */
public class ClientListeningThread extends Thread {
    private Client client;
    private InputStream in;
    private Serializer serializer;
    private ArrayBlockingQueue<Message> messageQueue;

    public ClientListeningThread(InputStream in, Client client) throws IOException {
        this.in = in;
        serializer = new Serializer();
        this.client = client;
        messageQueue = new ArrayBlockingQueue<>(100);
    }

    @Override
    public void run() {
        while (client.isRunning()) {
            try {
                //Read byte from stream to decide on which type of message is incoming
                byte type = (byte)in.read();

                //Socket is closed if the Stream returns -1
                if(type == (byte)-1){
                    throw new IOException("Cannot reach server");
                }
                //deserialize message from stream and put it into an message
                Message m = serializer.deserialize(in, type);

                //put the message into a queue for the MessageHandler to poll
                messageQueue.put(m);
            } catch (IOException ex) {
                System.err.println("Connection Lost: " + ex.toString() + "\nShutting down client!");
                client.getBridge().issueBox("Connection lost to server.",true);
                client.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.err.println("Shutting down receiving handler!");
    }

    public ArrayBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

}
