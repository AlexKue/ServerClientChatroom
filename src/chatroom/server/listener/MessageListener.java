package chatroom.server.listener;

import java.util.concurrent.SynchronousQueue;

import chatroom.server.listener.threads.UserThread;
import chatroom.model.Message;
import chatroom.serializer.MessageSerializer;

public class MessageListener extends Thread {
    private SynchronousQueue<Message> messageQueue;
    private NetworkListener networkListener;
    private MessageSerializer serializer;

    public MessageListener(NetworkListener networkListener) {
        messageQueue = new SynchronousQueue<>();        //Synchronized queue containing messages from clients
        this.networkListener = networkListener;
        serializer = new MessageSerializer();
    }
    
    @Override
    public void run(){
        while(true){
            if(!getMessageQueue().isEmpty()){
                //  "..." = deserialize(messageQueue.take());
            }
        }
    }

    public void serialize(){}
    public void deserialize(){}
    public void sendToTarget(){
    //TODO
    }

    public void sentToAll(){
        for(UserThread u : networkListener.getUserThreadList()){
            /* TODO */
        }
    }

    public SynchronousQueue<Message> getMessageQueue() {
        return messageQueue;
    }
}
