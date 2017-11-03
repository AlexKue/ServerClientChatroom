package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.TargetedTextMessage;
import java.io.BufferedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TargetedTextMessageSerializer extends UserMessageSerializer{


    @Override
    public void serialize(OutputStream out, Message m) {
        try {
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
            dataOut.writeByte((byte)2);
            dataOut.writeUTF(((TargetedTextMessage)m).getMessage());
            dataOut.writeUTF(((TargetedTextMessage)m).getSender());
            dataOut.writeUTF(((TargetedTextMessage)m).getReceiver());
            dataOut.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        String message = ((DataInputStream)in).readUTF();
        String sender = ((DataInputStream)in).readUTF();
        String receiver = ((DataInputStream)in).readUTF();
        return new TargetedTextMessage(message,sender,receiver);
    }
}
