package chatroom.client;

import chatroom.model.PublicTextMessage;
import chatroom.serializer.Serializer;

import java.io.OutputStream;
import java.util.Scanner;

public class ClientSendingThread extends Thread {
    private OutputStream out;
    private Scanner sc = new Scanner(System.in);
    private final Serializer serializer;
    private String name;

    public ClientSendingThread(OutputStream out){
        this.out = out;
        serializer = new Serializer();
    }

    @Override
    public void run(){
        //send a name (TODO: Proper Anthentification serverside)
        System.out.println("Enter your name");
        name = sc.nextLine();
        
        while (true){
            String stringMessage = sc.nextLine();
            PublicTextMessage message = new PublicTextMessage(stringMessage, name);
            serializer.serialize(out,(byte)1, message);
            
        }
    }

}
