package chatroom.client;

import chatroom.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientListeningThread extends Thread {
    ObjectInputStream dataIn;

    public ClientListeningThread(ObjectInputStream dataIn) throws IOException, ClassNotFoundException {
        this.dataIn = dataIn;
    }

    public void run(){
        try{
            Message m = (Message) dataIn.readObject();
            //TODO: Deserialize incoming message
        } catch (IOException e) {
            System.err.println("Error: Failed to retrieve message!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Received unknown object!");
            e.printStackTrace();
        }
    }

}
