package chatroom.client;

import chatroom.model.MessageTypeDictionary;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private MessageTypeDictionary messageTypeDictionary;
    private Scanner sc;
    private String name;

    public Client(){
        messageTypeDictionary = new MessageTypeDictionary();
        sc = new Scanner(System.in);
    }

    public void stop(){

    }

    public void start() {
        System.out.print("Please enter the adress of the server to connect to: ");
        String address = sc.nextLine();

        try {
            Socket server = new Socket(address, 54322);
            System.out.println("Connected to server!");

            //send a name (TODO: Proper Anthentification serverside)
            System.out.print("Enter your name: ");
            setName(sc.nextLine());

            InputStream dataIn = server.getInputStream();
            OutputStream dataOut = server.getOutputStream();

            ClientListeningThread clientListener = new ClientListeningThread(dataIn, this);
            ClientSendingThread clientSender = new ClientSendingThread(dataOut, this);
            
            clientListener.start();
            clientSender.start();
        } catch (IOException ex) {
            System.err.println("Connection Error! Couldn't connect so server!");
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) {

        Client client = new Client();
        client.start();

    }

    public MessageTypeDictionary getMessageTypeDictionary() {
        return messageTypeDictionary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
