package chatroom.server.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import chatroom.model.User;

public class NetworkListener extends Thread {

    private ServerSocket listener;
    private ArrayList<UserListeningThread> userListeningThreadList;

    public NetworkListener(ServerSocket listener) {
        this.userListeningThreadList = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Create new Thread for the MessageListener for each new client
                UserListeningThread u = new UserListeningThread(new User(listener.accept()));
                getUserListeningThreadList().add(u);
                u.start();


            } catch (IOException ex) {
                System.err.println("Connection between server and new client failed!");
                ex.printStackTrace();
            }
        }
    }

    public ArrayList<UserListeningThread> getUserListeningThreadList() {
        return userListeningThreadList;
    }
}
