package serverclientchatroom;


import java.io.IOException;
import java.net.ServerSocket;
import serverclientchatroom.listener.MessageListener;
import serverclientchatroom.listener.NetworkListener;

public class Server {
    public static void main (String args[]){

        try {
            ServerSocket listener = new ServerSocket(54322);
            NetworkListener networkListener = new NetworkListener(listener);
            MessageListener messageListener = new MessageListener(networkListener);
            System.out.println("Server Online!");
            networkListener.start();
            messageListener.start();
            
        } catch (IOException ex) {
            System.err.println("Error while starting the server!");
            ex.printStackTrace();
        }
        
    } 
    
}
