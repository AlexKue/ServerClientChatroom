package chatroom.client;

import chatroom.model.Message;
import chatroom.serializer.MessageSerializer;

import java.io.*;

import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        MessageSerializer s = new MessageSerializer();
        try {
            System.out.println("Please enter the adress of the server to connect to: ");
            String address = sc.nextLine();

            Socket server = new Socket(sc.nextLine(),54322);
            System.out.println("Connected to server!");

            ObjectInputStream dataIn = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));
            ObjectOutputStream dataOut = new ObjectOutputStream(new BufferedOutputStream(server.getOutputStream()));

            Thread clientListener = new Thread(() -> {
                try {
                    Message m = (Message)dataIn.readObject();
                    //TODO: Deserialize incoming message
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });



        } catch (IOException ex) {
            System.err.println("Connection Error!");
            ex.printStackTrace();
        }
    }
    
}
