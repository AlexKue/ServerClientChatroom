package serverclientchatroom.listener;

import java.util.ArrayList;
import serverclientchatroom.model.User;

public class MessageListener extends Thread {
    private ArrayList<User> UserList;
    private NetworkListener networkListener;

    public MessageListener(NetworkListener networkListener) {
        this.networkListener = networkListener;        
    }
    
    @Override
    public void run(){
        while(true){
            UserList = networkListener.getUserList();
        }
    }
    
}
