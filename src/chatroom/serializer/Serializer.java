package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.MessageTypeDictionary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class Serializer {

    private final HashMap<Byte, MessageSerializer> serializerHashMap;
    private final MessageTypeDictionary dict;

    public Serializer() {
        dict = new MessageTypeDictionary();
        serializerHashMap = new HashMap<>();
        serializerHashMap.put(dict.getByte(MessageType.PUBLICSERVERMSG), new PublicServerMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageType.PUBLICTEXTMSG), new PublicTextMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageType.TARGETTEXTMSG), new TargetedTextMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageType.LOGINMSG), new LoginMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageType.LOGOUTMSG), new LogoutMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageType.LOGINRESPONSEMSG), new LoginResponseSerializer());
        serializerHashMap.put(dict.getByte(MessageType.TARGETSERVERMSG), new TargetedServerMessageSerializer());
    }
    
    public void serialize(OutputStream out, Message m){
        serializerHashMap.get(m.getType()).serialize(out, m);
    }
    public Message deserialize(InputStream in, byte type) throws IOException{
        return serializerHashMap.get(type).deserialize(in);
    }
}
