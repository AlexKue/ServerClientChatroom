package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.PrivateChatStartRequestMessage;

import java.io.*;

public class PrivateChatStartRequestMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        PrivateChatStartRequestMessage message = (PrivateChatStartRequestMessage)m;
        dataOut.writeByte(dict.getByte(MessageType.PRIVATEROOMSTARTREQMSG));
        dataOut.writeUTF(message.getRequester());
        dataOut.writeUTF(message.getPartner());
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        String requester = dataIn.readUTF();
        String partner = dataIn.readUTF();
        return new PrivateChatStartRequestMessage(requester,partner);
    }
}
