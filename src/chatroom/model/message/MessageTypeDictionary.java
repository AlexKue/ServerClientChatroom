package chatroom.model.message;

import java.util.HashMap;

public class MessageTypeDictionary {

    private HashMap<Byte, MessageType> messageTypeHashMap;
    private HashMap<MessageType,Byte> reverseHashMap;

    public MessageTypeDictionary() {
        messageTypeHashMap = new HashMap<>();
        reverseHashMap = new HashMap<>();

        messageTypeHashMap.put((byte) 0, MessageType.PUBLICSERVERMSG);
        messageTypeHashMap.put((byte) 1, MessageType.PUBLICTEXTMSG);
        messageTypeHashMap.put((byte) 2, MessageType.TARGETTEXTMSG);
        messageTypeHashMap.put((byte) 3, MessageType.LOGINMSG);
        messageTypeHashMap.put((byte) 4, MessageType.LOGOUTMSG);
        messageTypeHashMap.put((byte) 5, MessageType.TARGETSERVERMSG);
        messageTypeHashMap.put((byte) 6, MessageType.LOGINRESPONSEMSG);
        messageTypeHashMap.put((byte) 7, MessageType.ROOMLISTMSG);

        for(byte key : messageTypeHashMap.keySet()){
            reverseHashMap.put(messageTypeHashMap.get(key), key);
        }
    }

    public MessageType getType(byte type) {
        return messageTypeHashMap.get(type);
    }

    public byte getByte(MessageType messageType){
        return reverseHashMap.get(messageType);
    }



}
