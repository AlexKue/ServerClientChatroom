package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.MessageTypeDictionary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * Handles serialization so that the listening/sending threads of the server
 * and client can send and receive those through the stream. Instead of using
 * <code>Serializable</code> interface, an own implementation is used.
 */

public abstract class MessageSerializer {
    protected final MessageTypeDictionary dict = new MessageTypeDictionary();


    /**
     * Prepares an Message object so that it can be sent through an 
     * <code>OutputStream</code>.
     * @param out an <code>OutputStream</code> to send the message through 
     */
    abstract public void serialize(OutputStream out, Message m);
    
    /**
     * Deserializes an incoming message of a client.
     * When the user of the client sends a message, a <code>Byte</code> is sent
     * to clarify which type of message is sent. Afterwards, multiple Objects 
     * may be sent, which this method receives and put them into an object 
     * inheriting <code>Message</code>
     *  
     * @param in the <code>InputStream</code> the message will received from
     * @return an Object inheriting the abstract class <code>Message</code>
     * @throws IOException 
     */
    abstract public Message deserialize(InputStream in) throws IOException;
}
