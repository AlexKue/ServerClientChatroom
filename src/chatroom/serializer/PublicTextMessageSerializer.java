package chatroom.serializer;

import chatroom.model.Message;
import chatroom.model.PublicTextMessage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class PublicTextMessageSerializer extends UserMessageSerializer {


    @Override
    public void serialize(OutputStream out, Message m) {
        try {
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
            dataOut.writeByte((byte)1);
            dataOut.writeUTF(((PublicTextMessage)m).getMessage());
            dataOut.writeUTF(((PublicTextMessage)m).getSender());
            dataOut.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        String message = dataIn.readUTF();
        String sender = dataIn.readUTF();
        return new PublicTextMessage(message,sender);
    }
}
