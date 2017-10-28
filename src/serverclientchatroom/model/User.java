package serverclientchatroom.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class User {
    private String name;
    private final Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public User(Socket socket) {  
        this.socket = socket;
        dataIn = new DataInputStream(new BufferedInputStream(getDataIn()));
        dataOut = new DataOutputStream(new BufferedOutputStream(getDataOut()));
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
