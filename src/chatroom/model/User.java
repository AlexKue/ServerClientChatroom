package chatroom.model;

import java.io.*;
import java.net.Socket;

public class User {
    private String name;
    private final Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public User(Socket socket) {
        this.socket = socket;
        try{
            dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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

    public DataInputStream getDataIn() {
        return dataIn;
    }

    public DataOutputStream getDataOut() {
        return dataOut;
    }
}
