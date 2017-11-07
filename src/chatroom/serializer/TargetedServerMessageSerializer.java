package chatroom.serializer;

import chatroom.model.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TargetedServerMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) {
        //TODO implement
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        return null;
    }
}
