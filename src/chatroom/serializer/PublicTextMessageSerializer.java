package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.PublicTextMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PublicTextMessageSerializer extends UserMessageSerializer {


    @Override
    public void serialize(OutputStream out) {

    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        String message = ((DataInputStream)in).readUTF();
        String sender = ((DataInputStream)in).readUTF();
        return new PublicTextMessage(message,sender);
    }
}
