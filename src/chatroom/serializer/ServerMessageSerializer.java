package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.ServerMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ServerMessageSerializer extends MessageSerializer{


    @Override
    public void serialize(OutputStream out) {
        //TODO
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        return new ServerMessage(((DataInputStream)in).readUTF());
    }
}
