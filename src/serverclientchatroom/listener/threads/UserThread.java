package serverclientchatroom.listener.threads;

import serverclientchatroom.listener.MessageListener;
import serverclientchatroom.model.User;

public class UserThread extends Thread {
    private User user;
    private MessageListener listener;

    public UserThread(User user){
        this.user = user;
    }

    public void run(){

    }

    public User getUser() {
        return user;
    }
}
