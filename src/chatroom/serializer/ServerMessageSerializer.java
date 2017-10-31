package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.ServerMessage;

public class ServerMessageSerializer extends MessageSerializer{

    public void serialize(Message m){

    }
    public ServerMessage deserialize(String m){
        return new ServerMessage(m);
    }
}
