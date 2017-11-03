package chatroom.model;

public abstract class Message {
    protected byte type;

    /**
     * Returns the type of Message, defined by a byte. 
     * Possible values: 0: ServerMessage
     *                  1: PublicTextMessage
     *                  2: TargetedTextMessage
     * @return 
     */
    public byte getType(){
        return type;
    }
}
