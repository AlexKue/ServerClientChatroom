package chatroom.model;

import java.util.HashMap;

public class MessageTypeDictionary {

   public enum MessageType{
      SERVERMSG, PUBLICTEXTMSG, TARGETTEXTMSG, LOGINMSG, LOGOUTMSG
   }
   public MessageType getType(byte type){
      return messageTypeHashMap.get(type);
   }

   private HashMap<Byte, MessageType> messageTypeHashMap;

   public MessageTypeDictionary(){
       messageTypeHashMap = new HashMap<>();
       messageTypeHashMap.put((byte)0,MessageType.SERVERMSG);
       messageTypeHashMap.put((byte)1,MessageType.PUBLICTEXTMSG);
       messageTypeHashMap.put((byte)2,MessageType.TARGETTEXTMSG);
       messageTypeHashMap.put((byte)3,MessageType.LOGINMSG);
       messageTypeHashMap.put((byte)4,MessageType.LOGOUTMSG);
   }


}
