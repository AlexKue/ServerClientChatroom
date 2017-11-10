package chatroom.server.listener;

import java.io.IOException;
import java.util.ArrayList;

import chatroom.model.UserConnectionInfo;
import chatroom.server.Server;

/**
 * This Thread handles accepting new Client on this server and removing Clients
 * when disconnecting.
 * @author ri93xuc
 */
public class NetworkListener extends Thread {

    private final Server server;
    private ArrayList<UserListeningThread> userListeningThreadList;

    public NetworkListener(Server server) {
        this.userListeningThreadList = new ArrayList<>();
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                UserConnectionInfo userConnectionInfo = new UserConnectionInfo(server.getListener().accept());
                System.out.println("Client connected: " + userConnectionInfo.getSocket().getInetAddress());
                //TODO: Thread pools (see executer)
                //Create new Thread for the MessageListener for each new client
                UserListeningThread u = new UserListeningThread(userConnectionInfo, server);
                getUserListeningThreadList().add(u);
                u.start();
            } catch (IOException ex) {
                System.out.println("*** Connection Error! " + ex.toString() + ") ***");
                //.accept() will throw if server is closing
            }

        }
        shutdown();
    }


    /**
     * Returns the Threadlist of ALL clients, even them being NOT logged in into
     * an account
     * @return a list of <code>UserListeningThreads</code>
     */
    public ArrayList<UserListeningThread> getUserListeningThreadList() {
        return userListeningThreadList;
    }
    
    public void shutdown(){
        System.out.println("*** Shutting down network listener ***");
        System.out.println("- Closing sockets of Client in List");
        for(UserListeningThread u : userListeningThreadList){
                removeClient(u);

        }
    }
    /**
     * Removes a Client from the UserlisteningThreadList by closing all of its
     * sockets and then removing it from the list
     * @param userThread the UserThread which should be removed
     */
    public void removeClient(UserListeningThread userThread) {
        UserConnectionInfo info = userThread.getUserConnectionInfo();
        try {
            info.getIn().close();
            info.getOut().close();
            info.getSocket().close();
        } catch (IOException e) {
            //We are closing sockets anyways
        } finally {
            info.setLoggedIn(false); //might not be necessary
        }
        userListeningThreadList.remove(userThread);
    }
}
