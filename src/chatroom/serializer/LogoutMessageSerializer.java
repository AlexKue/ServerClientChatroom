package chatroom.serializer;

import chatroom.model.message.LogoutMessage;
import chatroom.model.message.Message;
import chatroom.model.message.MessageType;

import java.io.*;

public class LogoutMessageSerializer extends MessageSerializer{
    @Override
    public void serialize(OutputStream out, Message m) throws IOException{
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.writeByte(dict.getByte(MessageType.LOGOUTMSG));
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        return new LogoutMessage();
    }
}
