package chatroom.serializer;

import chatroom.model.message.*;

import java.io.*;
import java.util.HashMap;

public class LoginResponseSerializer extends MessageSerializer {
    LoginResponsesDictionary responseDict = new LoginResponsesDictionary();

    @Override
    public void serialize(OutputStream out, Message m) {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        try {
            dataOut.writeByte(dict.getByte(MessageType.LOGINRESPONSEMSG));
            dataOut.writeByte(responseDict.getByte(((LoginResponseMessage) m).getResponse()));
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        return new LoginResponseMessage(responseDict.getType(dataIn.readByte()));
    }
}
