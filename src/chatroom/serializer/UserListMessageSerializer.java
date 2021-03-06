package chatroom.serializer;

import chatroom.model.message.Message;
import chatroom.model.message.MessageType;
import chatroom.model.message.RoomUserListMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserListMessageSerializer extends MessageSerializer{

    @Override
    public void serialize(OutputStream out, Message m) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        RoomUserListMessage message = (RoomUserListMessage)m;
        dataOut.writeByte(dict.getByte(MessageType.ROOMUSERLISTMSG));
        dataOut.writeUTF(message.getRoomName());
        dataOut.writeByte(message.getUserList().size());

        for(String u : message.getUserList()){
            dataOut.writeUTF(u);
        }
        dataOut.flush();

    }
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        List<String> userList = new ArrayList<>();
        String roomName = dataIn.readUTF();
        int size = dataIn.readByte();

        for(int i = 0; i < size; ++i){
            userList.add(dataIn.readUTF());
        }
        return new RoomUserListMessage(userList, roomName);
    }
}
