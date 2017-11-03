package chatroom.server.listener;

import chatroom.model.Message;
import chatroom.serializer.MessageSerializer;
import chatroom.serializer.PublicTextMessageSerializer;
import chatroom.serializer.ServerMessageSerializer;
import chatroom.serializer.TargetedTextMessageSerializer;
import chatroom.model.User;
import java.io.BufferedInputStream;
import java.io.DataInputStream;

import java.io.IOException;
import java.util.HashMap;

public class UserListeningThread extends Thread {

    private final User user;
    private MessageListener listener;
    public HashMap<Byte, MessageSerializer> serializerHashMap;
    private final DataInputStream userDataIn;

    public UserListeningThread(User user) {
        this.user = user;
        userDataIn = new DataInputStream(new BufferedInputStream(user.getIn()));
        serializerHashMap = new HashMap<>();
        serializerHashMap.put((byte) 0, new ServerMessageSerializer());
        serializerHashMap.put((byte) 1, new PublicTextMessageSerializer());
        serializerHashMap.put((byte) 2, new TargetedTextMessageSerializer());
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Get the serializer depending on incoming byte
                MessageSerializer serializer = serializerHashMap.get(userDataIn.readByte());
                Message m = serializer..deserialize(userDataIn);
                listener.getMessageQueue().put(m);
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
