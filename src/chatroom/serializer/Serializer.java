package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.MessageTypeDictionary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * This handles mapping of of <code>byte</code>s to a corresponding 
 * <code>MessageSerializer</code> and sending a message to the correct 
 * serializer.
 * NOTE: this class only maps and sends the message of an MessagSerializer! It
 * DOES NOT serializer itself.
 */
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
    
    /**
     * Sends a message to its corresponding Serializer
     * @param out the OutputStream of the client receiving the Message
     * @param m the Message being serialized
     */
    public void serialize(OutputStream out, Message m) throws IOException{
        serializerHashMap.get(m.getType()).serialize(out, m);
    }
    
    /**
     * Returns the message sent to the server by an client.
     * @param in the InputStream of the sender of the message
     * @param type the byte representing the type of message
     * @return the message being Sent
     * @throws IOException 
     */
    public Message deserialize(InputStream in, byte type) throws IOException{
        return serializerHashMap.get(type).deserialize(in);
    }
}
