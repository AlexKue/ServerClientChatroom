package chatroom.serializer;

import chatroom.model.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LogoutMessageSerializer extends MessageSerializer{
    @Override
    public void serialize(OutputStream out, Message m) {

    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        return null;
    }
}
