package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.PublicServerMessage;

import java.io.*;

public class PublicServerMessageSerializer extends MessageSerializer{


    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
            dataOut.writeByte(dict.getByte(MessageType.PUBLICSERVERMSG));
            dataOut.writeUTF(((PublicServerMessage)m).getMessage());
            dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        return new PublicServerMessage(dataIn.readUTF());
    }
}
