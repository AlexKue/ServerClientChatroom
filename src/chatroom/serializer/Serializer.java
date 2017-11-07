package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.MessageTypeDictionary;

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
        serializerHashMap.put(dict.getByte(MessageTypeDictionary.MessageType.PUBLICSERVERMSG), new PublicServerMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageTypeDictionary.MessageType.PUBLICTEXTMSG), new PublicTextMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageTypeDictionary.MessageType.TARGETTEXTMSG), new TargetedTextMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageTypeDictionary.MessageType.LOGINMSG), new LoginMessageSerializer());
        serializerHashMap.put(dict.getByte(MessageTypeDictionary.MessageType.LOGOUTMSG), new LogoutMessageSerializer());
    }
    
    public void serialize(OutputStream out, byte type, Message m){
        serializerHashMap.get(type).serialize(out, m);
    }
    public Message deserialize(InputStream in, byte type) throws IOException{
        return serializerHashMap.get(type).deserialize(in);
    }
}
