package chatroom.serializer;

import chatroom.model.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
    /*
    * The MessageSerializer is a class to handle different types of messages.
    * While for a simple chatprogram this might seem overengineered, it helps expanding the possibilities for
    * different types of messages to be send like files, whispering functionality or images if whished.
    * */

public abstract class MessageSerializer {
    abstract public void serialize(OutputStream out);
    abstract public Message deserialize(InputStream in) throws IOException;
}
