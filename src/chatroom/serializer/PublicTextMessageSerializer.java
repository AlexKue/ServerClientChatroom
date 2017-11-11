package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.PublicTextMessage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class PublicTextMessageSerializer extends UserMessageSerializer {


    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.writeByte(dict.getByte(MessageType.PUBLICTEXTMSG));
        dataOut.writeUTF(((PublicTextMessage) m).getMessage());
        dataOut.writeUTF(((PublicTextMessage) m).getSender());
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        String message = dataIn.readUTF();
        String sender = dataIn.readUTF();
        return new PublicTextMessage(message, sender);
    }
}
