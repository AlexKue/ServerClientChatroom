package chatroom.model;

import chatroom.server.room.Room;

import java.io.*;
import java.net.Socket;

/**
 * This contains information about the client of an user related to the connection like sockets and if the user is logged in.
 * It also contains an UserAccountInfo, which contains information about the user himself like names and the password.
 */
public class UserConnectionInfo {
    private UserAccountInfo userAccountInfo;
    private final Socket socket;
    private InputStream in;
    private OutputStream out;
    private boolean isLoggedIn;
    private Room activeRoom;

    public UserConnectionInfo(Socket socket) {
        this.socket = socket;
        setLoggedIn(false);
        try{
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException ex){
            System.err.println("Error: Failed to get I/O Streams of client!");
            ex.printStackTrace();
        }
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

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public UserAccountInfo getUserAccountInfo() {
        return userAccountInfo;
    }

    public void setUserAccountInfo(UserAccountInfo userAccountInfo) {
        this.userAccountInfo = userAccountInfo;
    }

    public Room getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(Room activeRoom) {
        this.activeRoom = activeRoom;
    }

}
