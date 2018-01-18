package chatroom.server.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import chatroom.model.UserConnectionInfo;
import chatroom.model.message.PublicServerMessage;
import chatroom.server.Server;

/**
 * This Thread handles accepting new Client on this server and removing Clients
 * when disconnecting.
 */
public class NetworkListener extends Thread {

    private final Server server;
    private List<UserListeningThread> userListeningThreadList; //List of all (also not logged in) clients

    public NetworkListener(Server server) {
        this.userListeningThreadList = Collections.synchronizedList(new ArrayList<>());
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                //Accept new Client and create new ConnectionInfo
                UserConnectionInfo userConnectionInfo = new UserConnectionInfo(server.getListener().accept());
                server.log(Level.INFO, "New Client Connected: " + userConnectionInfo.getSocket().getInetAddress());
                //Create new Thread for the MessageListener for each new client
                UserListeningThread u = new UserListeningThread(userConnectionInfo, server);
                getUserListeningThreadList().add(u);
                u.start();
            } catch (IOException ex) {
                server.log(Level.SEVERE, "NetworkListener: Exception while adding an client!",ex);
                //.accept() will throw if server is closing
            }
        }
        shutdown();
    }

    /**
     * Returns the ThreadList of ALL clients, even them being NOT logged in into
     * an account
     * @return a list of <code>UserListeningThreads</code>
     */
    public List<UserListeningThread> getUserListeningThreadList() {
        return userListeningThreadList;
    }

    /**
     * Closes the sockets and Streams of all users in the UserListeningThreadList
     */
    private void shutdown() {
        server.log(Level.SEVERE, "NetworkListener: Shutting down network listener");
        server.log(Level.SEVERE, "NetworkListener: Closing sockets of Client in List");

        Iterator<UserListeningThread> iter = userListeningThreadList.iterator();
        while (iter.hasNext()) {
            UserListeningThread u = iter.next();
            u.close();
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
        info.getActiveRoom().removeUser(info);
        //remove from List
        if (userListeningThreadList.remove(userThread)) {
            //Check if user is logged in
            if (info.isLoggedIn()) {
                info.setLoggedIn(false);
                //Notify logged in users that another logged in user left
                String username = info.getUserAccountInfo().getDisplayName();
                PublicServerMessage msg = new PublicServerMessage(username + " has left the server!");
                server.log(Level.INFO, username + "@" + info.getSocket().getInetAddress() + " has disconnected!");
                try {
                    server.getMessageListener().getMessageQueue().put(msg);
                } catch (InterruptedException e) {
                    server.log(Level.SEVERE, "NetworkListener: Error while sending Msg: ", e);
                }
            }
            //close Sockets
            userThread.close();
        }
        server.getBridge().updateUserListView(server.requestUserList());
    }

    /**
     * Removes a Client from the ThreadList by looking up the loginName, retrieving the Thread of the corresponding
     * name and passing it to <code>removeClient(UserListeningThread userThrad)</code>.
     * @param loginName the loginName of the User who should be removed
     */
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
