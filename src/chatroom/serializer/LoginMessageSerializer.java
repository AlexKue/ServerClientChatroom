package chatroom.serializer;

import chatroom.model.message.LoginMessage;
import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.MessageTypeDictionary;

import java.io.*;

public class LoginMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        try {
            dataOut.write(dict.getByte(MessageType.LOGINMSG));
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
