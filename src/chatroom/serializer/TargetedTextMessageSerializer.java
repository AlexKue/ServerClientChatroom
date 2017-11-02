package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.TargetedTextMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TargetedTextMessageSerializer extends UserMessageSerializer{


    @Override
    public void serialize(OutputStream out) {
        //TODO
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        String message = ((DataInputStream)in).readUTF();
        String sender = ((DataInputStream)in).readUTF();
        String receiver = ((DataInputStream)in).readUTF();
        return new TargetedTextMessage(message,sender,receiver);
    }
}
