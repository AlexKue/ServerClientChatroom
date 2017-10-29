package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.PublicTextMessage;

public class PublicTextMessageSerializer extends UserMessageSerializer {
    public PublicTextMessageSerializer() {
    }
    public PublicTextMessage serialize(String message, String sender){
        return new PublicTextMessage(message, sender);
    }

    @Override
    public String deserialize(Message m) {
        return ((PublicTextMessage)m).getMessage();
    }
}
