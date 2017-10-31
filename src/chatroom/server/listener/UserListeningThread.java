package chatroom.server.listener;

import chatroom.model.Message;
import chatroom.model.ServerMessage;
import chatroom.serializer.MessageSerializer;
import chatroom.serializer.PublicTextMessageSerializer;
import chatroom.serializer.ServerMessageSerializer;
import chatroom.serializer.TargetedTextMessageSerializer;
import chatroom.server.listener.MessageListener;
import chatroom.model.User;

import java.io.IOException;
import java.util.HashMap;

public class UserListeningThread extends Thread {
    private User user;
    private MessageListener listener;
    public HashMap<Byte, MessageSerializer> serializerHashMap;

    public UserListeningThread(User user){
        this.user = user;
        serializerHashMap = new HashMap<>();
        serializerHashMap.put((byte) 0,new ServerMessageSerializer());
        serializerHashMap.put((byte) 1, new PublicTextMessageSerializer());
        serializerHashMap.put((byte) 2, new TargetedTextMessageSerializer());
    }

    public void run(){
        try {
            Message m = deserialize(user.getDataIn().readByte());
            listener.getMessageQueue().put(m);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Message deserialize(Byte type){
        //TODO deserialize implementation
        return null;
    }


    public User getUser() {
        return user;
    }
}
