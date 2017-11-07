package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.MessageTypeDictionary;
import chatroom.model.TargetedTextMessage;
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
    public void serialize(OutputStream out, Message m) {
        try {
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
            dataOut.writeByte(dict.getByte(MessageTypeDictionary.MessageType.TARGETTEXTMSG));
            dataOut.writeUTF(((TargetedTextMessage) m).getMessage());
            dataOut.writeUTF(((TargetedTextMessage) m).getSender());
            dataOut.writeUTF(((TargetedTextMessage) m).getReceiver());
            dataOut.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
