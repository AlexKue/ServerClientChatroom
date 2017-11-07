package chatroom.model;

import java.io.*;
import java.net.Socket;

public class User {
    private String loginName;
    private final Socket socket;
    private InputStream in;
    private OutputStream out;
    private int id;
    private boolean isLoggedIn;

    public User(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
        setLoggedIn(false);
        try{
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException ex){
            System.err.println("Error: Failed to get I/O Streams of client!");
            ex.printStackTrace();
        }
    }


    public String getLoginName() {
        return loginName;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
