package chatroom.server.listener;

import java.io.IOException;
import java.util.ArrayList;

import chatroom.model.UserConnectionInfo;
import chatroom.model.UserStorage;
import chatroom.server.Server;

public class NetworkListener extends Thread {

    private Server server;
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



    public ArrayList<UserListeningThread> getUserListeningThreadList() {
        return userListeningThreadList;
    }

    public void shutdown(){
        System.out.println("*** Shutting down network listener ***");
        System.out.println("- Closing sockets of Client in List");
        for(UserListeningThread u : userListeningThreadList){
            try {
                u.getUserConnectionInfo().getIn().close();
                u.getUserConnectionInfo().getOut().close();
                u.getUserConnectionInfo().getSocket().close();
            } catch (IOException e) {
                //No need to handle since we close the server anyways
            }

        }
    }

    public void removeClient(UserListeningThread userThread) {
        UserConnectionInfo info = userThread.getUserConnectionInfo();
        try {
            info.getIn().close();
            info.getOut().close();
            info.getSocket().close();
        } catch (IOException e) {
            //We are closing sockets anyways
        } finally {
            info.setLoggedIn(false);
        }
        userListeningThreadList.remove(userThread);
    }
}
