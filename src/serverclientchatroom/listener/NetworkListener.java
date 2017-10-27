package serverclientchatroom.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import serverclientchatroom.model.User;

public class NetworkListener extends Thread {

    private ServerSocket listener;
    private ArrayList<User> UserList;

    public NetworkListener(ServerSocket listener) {
        this.UserList = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true) {
            try {
                getUserList().add(new User(listener.accept()));

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public ArrayList<User> getUserList() {
        return UserList;
    }

    public void setUserList(ArrayList<User> UserList) {
        this.UserList = UserList;
    }

}
