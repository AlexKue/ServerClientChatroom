package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.PublicServerMessage;
import java.io.BufferedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PublicServerMessageSerializer extends MessageSerializer{


    @Override
    public void serialize(OutputStream out, Message m) {
        try {
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
            dataOut.writeByte((byte)0);
            dataOut.writeUTF(((PublicServerMessage)m).getMessage());
            dataOut.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        return new PublicServerMessage(((DataInputStream)in).readUTF());
    }
}
