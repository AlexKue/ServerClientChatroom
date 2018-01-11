package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.WarningMessage;

import java.io.*;

public class WarningMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.writeByte(dict.getByte(MessageType.WARNINGMSG));
        WarningMessage message = (WarningMessage)m;
        dataOut.writeByte(message.getSeverity());
        dataOut.writeUTF(message.getMessage());
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        int severity = dataIn.readByte();
        String message = dataIn.readUTF();

        return new WarningMessage(severity,message);
    }
}
