package serverclientchatroom;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        try {
            Socket server = new Socket("localhost",54322);
            System.out.println("Connected to server!");
            
            InputStream in = server.getInputStream();
            OutputStream out = server.getOutputStream();
            DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
            
            while(true){
                dataOut.writeUTF(sc.nextLine());
                dataOut.flush();
                System.out.println(dataIn.readUTF());
            }
            
        } catch (IOException ex) {
            System.err.println("Connection Error!");
            ex.printStackTrace();
        }
    }
    
}
