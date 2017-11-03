/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.serializer;

import chatroom.model.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 *
 * @author ri93xuc
 */
public class Serializer {

    private final HashMap<Byte, MessageSerializer> serializerHashMap;

    public Serializer() {
        serializerHashMap = new HashMap<>();
        serializerHashMap.put((byte) 0, new ServerMessageSerializer());
        serializerHashMap.put((byte) 1, new PublicTextMessageSerializer());
        serializerHashMap.put((byte) 2, new TargetedTextMessageSerializer());
    }
    
    public void serialize(OutputStream out, Byte type){
        serializerHashMap.get(type).serialize(out);
    }
    public Message deserialize(InputStream in, Byte type) throws IOException{
        serializerHashMap.get(type).deserialize(in);
    }
}
