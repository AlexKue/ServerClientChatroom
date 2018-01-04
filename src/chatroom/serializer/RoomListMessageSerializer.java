package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.RoomListMessage;
import chatroom.model.message.RoomMessage;
import chatroom.server.room.Room;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomListMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        RoomListMessage roomListMessage = (RoomListMessage)m;
        dataOut.writeByte(dict.getByte(MessageType.ROOMLISTMSG));
        dataOut.writeByte(roomListMessage.getRoomList().size());

        for(RoomMessage r : roomListMessage.getRoomList()){
            dataOut.writeUTF(r.getName());
            dataOut.writeByte(r.getSize());
        }
        dataOut.flush();
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        int listSize = dataIn.readByte();
        List<RoomMessage> roomList = new ArrayList<>();

        for(int i = 0; i < listSize; ++i){
            roomList.add(new RoomMessage(dataIn.readUTF(),dataIn.readByte()));
        }
        RoomListMessage m = new RoomListMessage(roomList);
        return m;
    }
}
