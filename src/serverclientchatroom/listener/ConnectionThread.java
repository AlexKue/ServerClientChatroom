package serverclientchatroom.listener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionThread extends Thread {
    
    private Socket client;

    public ConnectionThread(Socket client) {
        this.client = client;
    }
    
    @Override
    public void run(){
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));     
        } catch (IOException ex) {
            Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
    
}
