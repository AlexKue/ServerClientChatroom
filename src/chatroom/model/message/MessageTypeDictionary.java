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
        messageTypeHashMap.put((byte) 8, MessageType.ROOMUSERLISTMSG);
        messageTypeHashMap.put((byte) 9, MessageType.ROOMCHANGEREQMSG);
        messageTypeHashMap.put((byte) 10, MessageType.WARNINGMSG);
        messageTypeHashMap.put((byte) 11, MessageType.SERVERUSERLISTMSG);
        messageTypeHashMap.put((byte) 12, MessageType.ROOMCHANGERESPONSEMSG);
        messageTypeHashMap.put((byte) 13, MessageType.ROOMNAMEEDITMSG);

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
