package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.RoomChangeResponseMessage;

import java.io.*;

public class RoomChangeResponseMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        RoomChangeResponseMessage message = ((RoomChangeResponseMessage)m);
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.writeByte(dict.getByte(MessageType.ROOMCHANGERESPONSEMSG));
        dataOut.writeBoolean(message.isSuccessful());
        dataOut.writeUTF(message.getRoomName());
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        boolean isSuccessFul = dataIn.readBoolean();
        String roomName = dataIn.readUTF();
        return new RoomChangeResponseMessage(isSuccessFul,roomName);
    }
}
