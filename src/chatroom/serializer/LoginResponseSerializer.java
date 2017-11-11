package chatroom.serializer;

import chatroom.model.message.*;

import java.io.*;
import java.util.HashMap;

public class LoginResponseSerializer extends MessageSerializer {
    LoginResponsesDictionary responseDict = new LoginResponsesDictionary();

    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.writeByte(dict.getByte(MessageType.LOGINRESPONSEMSG));
        dataOut.writeByte(responseDict.getByte(((LoginResponseMessage) m).getResponse()));
        dataOut.flush();

    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        return new LoginResponseMessage(responseDict.getType(dataIn.readByte()));
    }
}
