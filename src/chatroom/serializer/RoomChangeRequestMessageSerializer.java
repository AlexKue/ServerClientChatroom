package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.RoomChangeRequestMessage;

import java.io.*;

public class RoomChangeRequestMessageSerializer extends MessageSerializer{
    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        RoomChangeRequestMessage message = (RoomChangeRequestMessage)m;

        dataOut.writeByte(dict.getByte(MessageType.ROOMCHANGEREQMSG));
        dataOut.writeUTF(message.getLoginName());
        dataOut.writeUTF(message.getRoomName());
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        String loginName = dataIn.readUTF();
        String roomName = dataIn.readUTF();
        return new RoomChangeRequestMessage(roomName,loginName);
    }
}
