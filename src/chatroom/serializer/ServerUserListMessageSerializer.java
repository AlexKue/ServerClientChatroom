package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.ServerUserListMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerUserListMessageSerializer extends MessageSerializer {

    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        ServerUserListMessage message = (ServerUserListMessage)m;
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.writeByte(dict.getByte(MessageType.SERVERUSERLISTMSG));
        dataOut.writeByte(message.getServerUserList().size());

        for(String u : message.getServerUserList()){
            dataOut.writeUTF(u);
        }
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        List<String> serverUserList = new ArrayList<>();
        int size = dataIn.readByte();

        for(int i = 0; i < size; ++i){
            serverUserList.add(dataIn.readUTF());
        }

        ServerUserListMessage m = new ServerUserListMessage(serverUserList);
        return m;
    }
}
