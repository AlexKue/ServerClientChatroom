package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.MessageTypeDictionary;
import chatroom.model.message.TargetedTextMessage;

import java.io.*;

public class TargetedTextMessageSerializer extends UserMessageSerializer {

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        String message = dataIn.readUTF();
        String sender = dataIn.readUTF();
        String receiver = dataIn.readUTF();
        return new TargetedTextMessage(message, sender, receiver);
    }

    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.writeByte(dict.getByte(MessageType.TARGETTEXTMSG));
        dataOut.writeUTF(((TargetedTextMessage) m).getMessage());
        dataOut.writeUTF(((TargetedTextMessage) m).getSender());
        dataOut.writeUTF(((TargetedTextMessage) m).getReceiver());
        dataOut.flush();
    }
}
