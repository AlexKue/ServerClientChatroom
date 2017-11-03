package chatroom.server.listener;

import chatroom.model.Message;
import chatroom.model.User;
import chatroom.serializer.Serializer;
import java.io.BufferedInputStream;
import java.io.DataInputStream;

import java.io.IOException;
import java.util.HashMap;

public class UserListeningThread extends Thread {

    private final User user;
    private MessageListener listener;
    private final DataInputStream userDataIn;
    private final Serializer serializer;

    public UserListeningThread(User user) {
        this.user = user;
        userDataIn = new DataInputStream(new BufferedInputStream(user.getIn()));
        serializer = new Serializer();

    }

    @Override
    public void run() {
        while (true) {
            try {               
                byte type = userDataIn.readByte();
                Message m = serializer.deserialize(userDataIn, type);
                listener.getMessageQueue().put(m); //TODO: get Messagequeue somehow
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUser() {
        return user;
    }
}
