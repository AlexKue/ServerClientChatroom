package chatroom.model.message;

import chatroom.model.UserConnectionInfo;

/**
 * This is the basic class of a Message.
 * All Messages which should be sent to a client and can be received
 * have to inherit from this class.
 * All messages, which can bin instanced, have to have a byte. This byte
 * is needed to help the serializer to classify what type of message is currently
 * incoming. Each Message has its corresponding <code>MessageSerializer</code>
 */
public abstract class Message {
    protected byte type;
    private transient UserConnectionInfo connectionInfo;

    /**
     * Returns the byte representing the message
     * @return 
     */
    public byte getType(){
        return type;
    }

    /**
     * Returns UserConnectionInfo of an Client.
     * NOTE: After deserialization of Messages, this <code>UserConnectionInfo</code>
     * contains information of the sender of an Message, but ServerMessages
     * often contain Information about receivers of this message, since servers itself
     * are the senders.
     * While it is preferred to find out information of the message by the
     * contents itself, there are situations where there are no means of
     * getting the identity of the sender of this message. For example, you can't
     * verify a <code>LoginMessage</code> by the loginName, because it might be wrong and/or not available 
     * in the <code>UserStorage</code>. Also, if a server needs to send a message as a response, it can retrieve the 
     * Streams of the clients of the incoming message and simply put this information on the ServerMessage itself.
     * <code>PublicTextMessages</code> on the other hand should just retrieve 
     * Targets on the UserThreadList of its MessageHandler.
     * @return the UserConnectionInfo of this message.
     */
    public UserConnectionInfo getUserConnectionInfo() {
        return connectionInfo;
    }
    
     /**
     * Sets the UserConnectionInfo of an Client into a Message
     * NOTE: After deserialization of Messages, this <code>UserConnectionInfo</code>
     * contains information of the sender of an Message, but ServerMessages
     * often contain Information about receivers of this message, since servers itself
     * are the senders.
     * While it is preferred to find out information of the message by the
     * contents itself, there are situations where there are no means of
     * getting the identity of the sender of this message. For example, you can't
     * verify a <code>LoginMessage</code> by the loginName, because it might be wrong and/or not available 
     * in the <code>UserStorage</code>. Also, if a server needs to send a message as a response, it can retrieve the 
     * Streams of the clients of the incoming message and simply put this information on the ServerMessage itself.
     * <code>PublicTextMessages</code> on the other hand should just retrieve 
     * Targets on the UserThreadList of its MessageHandler.
     * @return the UserConnectionInfo of this message.
     */
    public void setUserConnectionInfo(UserConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
}
