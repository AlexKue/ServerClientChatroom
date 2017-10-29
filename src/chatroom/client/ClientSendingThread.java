package chatroom.client;

import chatroom.model.Message;

import java.io.ObjectOutputStream;
import java.util.Scanner;

public class ClientSendingThread extends Thread {
    ObjectOutputStream dataOut;
    Scanner sc = new Scanner(System.in);

    public ClientSendingThread(ObjectOutputStream dataOut){
        this.dataOut = dataOut;
    }

    @Override
    public void run(){
        while (true){
            String message = sc.nextLine();
            //TODO Serialization
        }
    }

}
