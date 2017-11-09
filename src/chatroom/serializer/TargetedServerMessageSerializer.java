package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.MessageTypeDictionary;
import chatroom.model.message.TargetedServerMessage;

import java.io.*;

public class TargetedServerMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        try {
            dataOut.writeByte(dict.getByte(MessageType.TARGETSERVERMSG));
            dataOut.writeUTF(((TargetedServerMessage)m).getMessage());
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        String message = dataIn.readUTF();
        return new TargetedServerMessage(message);
    }
}
