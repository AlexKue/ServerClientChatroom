package chatroom.server.listener.threads;

import chatroom.server.listener.MessageListener;
import chatroom.model.User;

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
