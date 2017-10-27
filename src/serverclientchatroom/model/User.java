package serverclientchatroom.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class User {
    private String name;
    private final Socket socket;
    private DataInputStream dIn;
    private DataOutputStream dOut;

    public User(Socket socket) {  
        this.socket = socket;
        dIn = new DataInputStream(new BufferedInputStream(dIn));
        dOut = new DataOutputStream(new BufferedOutputStream(dOut));
    }

   

    
}
