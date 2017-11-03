package chatroom.client;

import chatroom.model.Message;
import chatroom.serializer.Serializer;
import java.io.BufferedInputStream;
import java.io.DataInputStream;

import java.io.IOException;
import java.io.InputStream;

public class ClientListeningThread extends Thread {
    InputStream in;
    Serializer serializer;

    public ClientListeningThread(InputStream in) throws IOException, ClassNotFoundException {
        this.in = in;
        serializer = new Serializer();
    }

    public void run(){
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        while(true){
            try {
                byte type = dataIn.readByte();
                Message m = serializer.deserialize(in, type);
                //TODO: Display text
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
