package chatroom.server.listener;

import chatroom.model.Message;
import chatroom.server.listener.MessageListener;
import chatroom.model.User;

import java.io.IOException;

public class UserListeningThread extends Thread {
    private User user;
    private MessageListener listener;

    public UserListeningThread(User user){
        this.user = user;
    }

    public void run(){
        try {
            listener.getMessageQueue().put((Message)user.getDataIn().readObject());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public User getUser() {
        return user;
    }
}
