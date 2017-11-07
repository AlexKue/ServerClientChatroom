package chatroom.server.listener;

import chatroom.model.Message;
import chatroom.model.PublicTextMessage;
import chatroom.model.ServerMessage;
import chatroom.model.User;
import chatroom.serializer.Serializer;
import chatroom.server.Server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;

import java.io.IOException;
import java.util.HashMap;

public class UserListeningThread extends Thread {

    private final User user;
    private Server server;
    private final Serializer serializer;

    public UserListeningThread(User user, Server server) {
        this.server = server;
        this.user = user;
        serializer = new Serializer();

    }

    @Override
    public void run() {
        while(!getUser().isLoggedIn()){
            ServerMessage m = new ServerMessage("Please enter your login name: ");
            server.getMessageListener().sendToTarget(m, user.getId());
            String loginName = serializer.deserialize(user.getIn(), )
            //TODO: login process
        }
        if(getUser().isLoggedIn()){
            listen();
        }

    }

    private void listen() {
        boolean isRunning = true;
        while (isRunning) {
            try {
                //ready byte to decide which type of message is sent
                byte type = (byte) user.getIn().read();
                //deserialize message, create new Message Object
                Message m = serializer.deserialize(user.getIn(), type);
                m.setId(user.getId());
                server.getMessageListener().getMessageQueue().put(m);
            } catch (IOException e) {
                System.err.println("Lost Connection to client!");
                isRunning = false; //Stop the Thread if connection is lost
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUser() {
        return user;
    }
}
