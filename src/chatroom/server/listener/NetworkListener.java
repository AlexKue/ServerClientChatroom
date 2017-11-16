package chatroom.server.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import chatroom.model.UserConnectionInfo;
import chatroom.model.message.PublicServerMessage;
import chatroom.server.Server;

/**
 * This Thread handles accepting new Client on this server and removing Clients
 * when disconnecting.
 */
public class NetworkListener extends Thread {

    private final Server server;
    private List<UserListeningThread> userListeningThreadList;

    public NetworkListener(Server server) {
        this.userListeningThreadList = Collections.synchronizedList(new ArrayList<>());
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                UserConnectionInfo userConnectionInfo = new UserConnectionInfo(server.getListener().accept());
                System.out.println("Client connected: " + userConnectionInfo.getSocket().getInetAddress());
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
     * Returns the ThreadList of ALL clients, even them being NOT logged in into
     * an account
     *
     * @return a list of <code>UserListeningThreads</code>
     */
    public List<UserListeningThread> getUserListeningThreadList() {
        return userListeningThreadList;
    }

    public void shutdown() {
        System.out.println("*** Shutting down network listener ***");
        System.out.println("- Closing sockets of Client in List");
        for (UserListeningThread u : userListeningThreadList) {
            if (u != null) {
                removeClient(u);
            }
        }
    }

    /**
     * Removes a Client from the UserListeningThreadList by closing all of its
     * sockets and then removing it from the list
     *
     * @param userThread the UserThread which should be removed
     */
    public void removeClient(UserListeningThread userThread) {
        //set loggedInStatus to False
        UserConnectionInfo info = userThread.getUserConnectionInfo();
        info.setLoggedIn(false); //might not be necessary

        //remove from List
        if (userListeningThreadList.remove(userThread)) {

            //Notify logged in users that someone left
            String username = info.getUserAccountInfo().getDisplayName();
            PublicServerMessage msg = new PublicServerMessage(username + " has left the server!");
            try {
                server.getMessageListener().getMessageQueue().put(msg);
            } catch (InterruptedException e) {
                System.err.println("Error while sending Msg: " + e.toString());
            }

            try {
                info.getIn().close();
                info.getOut().close();
                info.getSocket().close();
            } catch (IOException e) {
                //We are closing sockets anyways
            }
        }

    }

    public void removeClient(String loginName) {
        Iterator<UserListeningThread> iter = userListeningThreadList.iterator();

        while (iter.hasNext()) {
            UserListeningThread u = iter.next();
            UserConnectionInfo info = u.getUserConnectionInfo();
            if (info.isLoggedIn() && info.getUserAccountInfo().getLoginName().equals(loginName)) {
                removeClient(u);
                break;
            }
        }
    }
}
