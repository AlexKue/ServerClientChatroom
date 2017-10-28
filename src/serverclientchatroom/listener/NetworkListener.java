package serverclientchatroom.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import serverclientchatroom.listener.threads.UserThread;
import serverclientchatroom.model.User;

public class NetworkListener extends Thread {

    private ServerSocket listener;
    private ArrayList<UserThread> userThreadList;

    public NetworkListener(ServerSocket listener) {
        this.userThreadList = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Create new Thread for the MessageListener for each new client
                UserThread u = new UserThread(new User(listener.accept()));
                getUserThreadList().add(u);
                u.start();

            } catch (IOException ex) {
                System.err.println("Connection between server and new client failed!");
                ex.printStackTrace();
            }

        }
    }

    public ArrayList<UserThread> getUserThreadList() {
        return userThreadList;
    }

    public void setUserThreadList(ArrayList<UserThread> UserList) {
        this.userThreadList = UserList;
    }

}
