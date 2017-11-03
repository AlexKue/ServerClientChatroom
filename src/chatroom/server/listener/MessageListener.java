package chatroom.server.listener;

import chatroom.model.Message;
import chatroom.serializer.Serializer;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageListener extends Thread {
    private final ArrayBlockingQueue<Message> messageQueue;
    private final NetworkListener networkListener;
    private final Serializer serializer;

    public MessageListener(NetworkListener networkListener) {
        messageQueue = new ArrayBlockingQueue<>(100);  //Synchronized queue containing messages from clients
        this.networkListener = networkListener;
        serializer = new Serializer();
    }
    
    @Override
    public void run(){
        while(true){
            if(!getMessageQueue().isEmpty()){
                try {
                    Message m = messageQueue.take();
                    //Byte sent by client decides which type of message is sent
                    switch(m.getType()){
                        case 0: sendToAll(m);
                        case 1: sendToTarget(m);
                        case 2: sendToAll(m);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendToTarget(Message m){
        //TODO Serialize
        
    }

    public void sendToAll(Message m){
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            //TODO Serialize
        }
    }


    public ArrayBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }
}
