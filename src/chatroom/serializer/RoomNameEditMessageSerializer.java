package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.RoomNameEditMessage;

import java.io.*;

public class RoomNameEditMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.writeByte(dict.getByte(MessageType.ROOMNAMEEDITMSG));
        dataOut.writeUTF(((RoomNameEditMessage)m).getNewName());
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        return new RoomNameEditMessage(dataIn.readUTF());
    }
}
