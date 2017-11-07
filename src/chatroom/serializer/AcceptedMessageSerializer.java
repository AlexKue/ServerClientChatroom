package chatroom.serializer;

import chatroom.model.AcceptedMessage;
import chatroom.model.Message;

import java.io.*;

public class AcceptedMessageSerializer extends MessageSerializer {
    @Override
    public void serialize(OutputStream out, Message m) {
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        try {
            dataOut.writeBoolean(((AcceptedMessage)m).getAcceptanceStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Message deserialize(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        return new AcceptedMessage(dataIn.readBoolean());
    }
}
