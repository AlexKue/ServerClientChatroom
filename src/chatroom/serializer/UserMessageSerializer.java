package chatroom.serializer;

import chatroom.model.Message;

public abstract class UserMessageSerializer extends MessageSerializer{
    abstract public String deserialize(Message m);
}
