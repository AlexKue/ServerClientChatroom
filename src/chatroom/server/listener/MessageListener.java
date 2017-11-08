package chatroom.server.listener;

import chatroom.model.Message;
import chatroom.model.TargetedTextMessage;
import chatroom.model.UserStorage;
import chatroom.serializer.Serializer;
import chatroom.server.Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageListener extends Thread {
    private final ArrayBlockingQueue<Message> messageQueue;
    private Server server;
    private final Serializer serializer;
    private UserStorage userStorage;

    public MessageListener(Server server) {
        messageQueue = new ArrayBlockingQueue<>(100);  //Synchronized queue containing messages from clients
        this.server = server;
        serializer = new Serializer();
        userStorage = new UserStorage();
    }
    
    @Override
    public void run(){
        while(server.isRunning()){
                try {
                    Message m = messageQueue.poll(1, TimeUnit.SECONDS);
                    if (m == null){
                        continue;
                    }

                    //Byte sent by client decides which type of message is sent
                    switch(server.getMessageTypeDictionary().getType(m.getType())){
                        case PUBLICSERVERMSG: case PUBLICTEXTMSG:
                            sendToAll(m);
                            break;
                        case TARGETTEXTMSG:
                            sendToTarget(m, ((TargetedTextMessage)m).getReceiver());
                            break;
                        case LOGINMSG:
                            authentificate(m);
                    }
                } catch (InterruptedException e) {
                    System.err.println("*** MessageListener interrupted by server! ***");
                }
        }
    }

    private void authentificate(Message m) {

    }

    public void sendToTarget(Message m, String loginName){
        for(UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()){
            if(u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(loginName)){
                serializer.serialize(u.getUserConnectionInfo().getOut(), m.getType(), m);
                break;
            }
        }
    }
    public void sendToAll(Message m){
        for(UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn()){
                serializer.serialize(u.getUserConnectionInfo().getOut(), m.getType(), m);
            }
        }
    }


    public ArrayBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }
}
