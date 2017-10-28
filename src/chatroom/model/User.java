package chatroom.model;

import java.io.*;
import java.net.Socket;

public class User {
    private String name;
    private final Socket socket;
    private ObjectInputStream dataIn;
    private ObjectOutputStream dataOut;

    public User(Socket socket) {
        this.socket = socket;
        try{
            dataIn = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOut = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ex){
            System.err.println("Error: Failed to get I/O Streams of client!");
            ex.printStackTrace();
        }
    }


    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getDataIn() {
        return dataIn;
    }

    public ObjectOutputStream getDataOut() {
        return dataOut;
    }
}
