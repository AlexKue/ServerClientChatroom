package chatroom.server.listener;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

import chatroom.model.Message;

public class MessageListener extends Thread {
    private SynchronousQueue<Message> messageQueue;
    private NetworkListener networkListener;

    public MessageListener(NetworkListener networkListener) {
        messageQueue = new SynchronousQueue<>();        //Synchronized queue containing messages from clients
        this.networkListener = networkListener;
    }
    
    @Override
    public void run(){
        while(true){
            if(!getMessageQueue().isEmpty()){
                try {
                    Message m = messageQueue.take();
                    //TODO: Deserialize message
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendToTarget(){
    //TODO
    }

    public void sentToAll(Message m){
        for(UserListeningThread u : networkListener.getUserListeningThreadList()){
            /* TODO */
            try {
                u.getUser().getDataOut().writeObject(m);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void serialize(Byte type, Message m){
        switch(type){
            //TODO
        }
    }
    private void deserialize(Byte type, Message m){
        switch(type){
            //TODO
        }
    }

    public SynchronousQueue<Message> getMessageQueue() {
        return messageQueue;
    }
}
