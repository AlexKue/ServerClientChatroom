package chatroom.serializer;

import chatroom.model.LoginMessage;
import chatroom.model.Message;
import chatroom.model.MessageTypeDictionary;

import javax.xml.crypto.Data;
import java.io.*;

public class LoginMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        try {
            dataOut.writeByte(dict.getByte(MessageTypeDictionary.MessageType.LOGINMSG));
            dataOut.writeUTF(((LoginMessage)m).getLoginName());
            dataOut.writeUTF(((LoginMessage)m).getPassword());
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        String loginName = dataIn.readUTF();
        String password = dataIn.readUTF();
        return new LoginMessage(loginName,password);
    }
}
