package chatroom.model;

import java.io.*;
import java.net.Socket;

public class User {
    private String name;
    private final Socket socket;
    private InputStream in;
    private OutputStream out;

    public User(Socket socket) {
        this.socket = socket;
        try{
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }
}
