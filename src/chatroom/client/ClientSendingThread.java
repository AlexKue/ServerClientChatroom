package chatroom.client;

import chatroom.model.MessageTypeDictionary;
import chatroom.model.PublicTextMessage;
import chatroom.serializer.Serializer;

import java.io.OutputStream;
import java.util.Scanner;

public class ClientSendingThread extends Thread {
    private OutputStream out;
    private Scanner sc = new Scanner(System.in);
    private final Serializer serializer;
    private final Client client;

    public ClientSendingThread(OutputStream out, Client client){
        this.client = client;
        this.out = out;
        serializer = new Serializer();
    }

    @Override
    public void run(){
        while (true){
            String stringMessage = sc.nextLine();
            PublicTextMessage message = new PublicTextMessage(stringMessage, client.getName());
            serializer.serialize(out, (byte)1, message);
            
        }
    }

}
