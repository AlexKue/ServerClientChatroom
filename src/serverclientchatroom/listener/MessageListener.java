package serverclientchatroom.listener;

import java.util.concurrent.SynchronousQueue;

import serverclientchatroom.listener.threads.UserThread;
import serverclientchatroom.model.Message;

public class MessageListener extends Thread {
    private SynchronousQueue<Message> messageQueue;
    private NetworkListener networkListener;

    public MessageListener(NetworkListener networkListener) {
        messageQueue = new SynchronousQueue<>();
        this.networkListener = networkListener;
    }
    
    @Override
    public void run(){
        while(true){
            if(!messageQueue.isEmpty()){
                //TODO
            }
        }
    }
    public void sendToTarget(){
    //TODO
    }

    public void sentToAll(){
        for(UserThread u : networkListener.getUserThreadList()){
            /* TODO */
        }
    }
}
