package chatroom.server.listener;

import chatroom.model.message.Message;
import chatroom.model.UserConnectionInfo;
import chatroom.serializer.Serializer;
import chatroom.server.Server;

import java.io.IOException;

public class UserListeningThread extends Thread {

    private final UserConnectionInfo userConnectionInfo;
    private Server server;
    private final Serializer serializer;

    public UserListeningThread(UserConnectionInfo userConnectionInfo, Server server) {
        this.server = server;
        this.userConnectionInfo = userConnectionInfo;
        serializer = new Serializer();
    }

    @Override
    public void run() {
            listen();
    }

    private void listen() {
        boolean isRunning = true;
        while (isRunning) {
            try {
                //ready byte to decide which type of message is sent
                byte type = (byte) userConnectionInfo.getIn().read();

                //deserialize message, create new Message Object
                Message m = serializer.deserialize(userConnectionInfo.getIn(), type);

                //put UserConnectionInfo into the message
                m.setUserConnectionInfo(userConnectionInfo);

                //put message into queue
                server.getMessageListener().getMessageQueue().put(m);
            } catch (IOException e) {
                System.err.println("Lost Connection to client!");
                isRunning = false; //Stop the Thread if connection is lost
               // server.getNetworkListener().removeClient(this);
            } catch (InterruptedException e) {
                System.out.println("UserListening Thread interrupted!");
                e.printStackTrace();
              //  server.getNetworkListener().removeClient(this);
                isRunning = false;
            }
        }
    }

    public UserConnectionInfo getUserConnectionInfo() {
        return userConnectionInfo;
    }
}
