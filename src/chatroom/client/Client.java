package chatroom.client;

import chatroom.model.Message;
import chatroom.serializer.MessageSerializer;

import java.io.*;

import java.net.Socket;
import java.util.Scanner;

public class Client {

    public void init() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Please enter the adress of the server to connect to: ");
            String address = sc.nextLine();

            Socket server = new Socket(address, 54322);
            System.out.println("Connected to server!");

            InputStream dataIn = server.getInputStream();
            OutputStream dataOut = server.getOutputStream();

            ClientListeningThread clientListener = new ClientListeningThread(dataIn);
            ClientSendingThread clientSender = new ClientSendingThread(dataOut);
            
            clientListener.start();
            clientSender.start();
        } catch (IOException ex) {
            System.err.println("Connection Error!");
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        Client client = new Client();
        client.init();

    }
}
