package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.TargetedTextMessage;

public class TargetedTextMessageSerializer extends UserMessageSerializer{

    @Override
    public String deserialize(Message m) {
        return ((TargetedTextMessage)m).getMessage()+"";
    }
}
