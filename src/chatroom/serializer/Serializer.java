package chatroom.serializer;

import chatroom.model.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class Serializer {

    private final HashMap<Byte, MessageSerializer> serializerHashMap;

    public Serializer() {
        serializerHashMap = new HashMap<>();
        serializerHashMap.put((byte) 0, new ServerMessageSerializer());
        serializerHashMap.put((byte) 1, new PublicTextMessageSerializer());
        serializerHashMap.put((byte) 2, new TargetedTextMessageSerializer());
    }
    
    public void serialize(OutputStream out, byte type, Message m){
        serializerHashMap.get(type).serialize(out, m);
    }
    public Message deserialize(InputStream in, byte type) throws IOException{
        return serializerHashMap.get(type).deserialize(in);
    }
}
