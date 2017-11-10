package chatroom.server.listener;

import chatroom.model.*;
import chatroom.model.message.*;
import chatroom.model.message.Message;
import chatroom.serializer.Serializer;
import chatroom.server.Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This receives messages from all Clients connected on the server, puts them
 * into a queue and sends the message to one ore more Clients.
 * If the message is for example login-related, it also handles registration
 * and authentication of the Client.
 */
public class MessageListener extends Thread {
    private final ArrayBlockingQueue<Message> messageQueue;
    private final Server server;
    private final Serializer serializer;
    private final UserStorage userStorage;

    public MessageListener(Server server) {
        messageQueue = new ArrayBlockingQueue<>(100);  //Synchronized queue containing messages from clients
        this.server = server;
        serializer = new Serializer(); //Serializes and sends messages through stream
        userStorage = new UserStorage(); //Contains data of user registered on this server, at runtime for now
    }
    //TODO: UserListMessage
    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                Message m = messageQueue.poll(1, TimeUnit.SECONDS);
                if (m == null) {
                    continue;
                }
                //Display on server what it is working: 
                String messageTypeString = server.getMessageTypeDictionary().getType(m.getType()).toString();               
                System.out.println("MessageListener: Now working on: " + messageTypeString);
                
                //Byte sent by client decides which type of message is sent
                switch (server.getMessageTypeDictionary().getType(m.getType())) {
                    case PUBLICSERVERMSG:
                    case PUBLICTEXTMSG:
                        sendToAll(m);
                        break;
                    case TARGETTEXTMSG:
                    case TARGETSERVERMSG:
                    case LOGINRESPONSEMSG:
                        sendToTarget(m);
                        break;
                    case LOGINMSG:
                        authenticate(m);
                        break;
                }
                System.out.println("MessageListener: Sent message(s): " + messageTypeString);
                //Issues may occur on clients that message are sent too fast.
                //TODO: find more elegant fix, if there is any
                sleep(50);
            } catch (InterruptedException e) {
                System.err.println("*** MessageListener interrupted by server! ***");
            }
        }
    }

    /**
     * Checks the login data of an user and sends <code>LoginResponseMessages</code>
     * back to the user
     *
     * @param m the message containing loginname and password
     * @throws InterruptedException if the queue gets interrupted
     */
    private void authenticate(Message m) throws InterruptedException {
        LoginMessage loginMessage = (LoginMessage) m;


        //check the storage if username is registered on this server
        if (!userStorage.loginNameExists(loginMessage.getLoginName())) {

            //send Response Message: No one uses this name
            LoginResponseMessage message = new LoginResponseMessage(LoginResponses.CREATED_ACCOUNT);
            message.setUserConnectionInfo(m.getUserConnectionInfo());
            messageQueue.put(message);

            //set User as logged in
            m.getUserConnectionInfo().setLoggedIn(true);

            //add account info to connection info
            userStorage.addClient(loginMessage.getLoginName(), loginMessage.getPassword());
            UserAccountInfo userInfo = userStorage.getUserAccountInfo(loginMessage.getLoginName());
            m.getUserConnectionInfo().setUserAccountInfo(userInfo);

            System.out.println("Created new account for " + loginMessage.getLoginName());

            //send list of users to new Client
            messageQueue.put(buildUserListMessage(m.getUserConnectionInfo()));

            //Notify other users that someone connected
            messageQueue.put(new PublicServerMessage(userInfo.getDisplayName() + " has connected to the Server!"));
            return;

        } else {
            //case: username is registered
            //check if password is correct
            boolean success = userStorage.checkLogin(loginMessage.getLoginName(), loginMessage.getPassword());

            LoginResponseMessage response;
            if (success) {
                //check if another user is already using the account
                for (UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()) {

                    //Get the ConnectionInfo of the client
                    UserConnectionInfo userInfo = u.getUserConnectionInfo();

                    //check in the list if an user has already taken the name
                    if (userInfo.isLoggedIn() && userInfo.getUserAccountInfo().getLoginName().equals(loginMessage.getLoginName())) {
                        LoginResponseMessage message = new LoginResponseMessage(LoginResponses.ALREADY_LOGGED_IN);
                        message.setUserConnectionInfo(m.getUserConnectionInfo());
                        messageQueue.put(message);

                        System.out.println("A client tried to log into an account which was already online! ");
                        return;
                    }

                }

                //No one else uses this account, grant access
                response = new LoginResponseMessage(LoginResponses.SUCCESS);
                m.getUserConnectionInfo().setLoggedIn(true);

                //set AccountInfo for the connection
                UserAccountInfo accountInfo = userStorage.getUserAccountInfo(loginMessage.getLoginName());
                m.getUserConnectionInfo().setUserAccountInfo(accountInfo);
                System.out.println("User " + loginMessage.getLoginName() + " has logged in");

                //Send new User list of already logged in users
                messageQueue.put(buildUserListMessage(m.getUserConnectionInfo()));

                //Notify other users that someone connected
                messageQueue.put(new PublicServerMessage(accountInfo.getDisplayName() + " has connected to the Server!"));
            } else {
                response = new LoginResponseMessage(LoginResponses.WRONG_PASSWORD);
                System.out.println("Someone failed to login into the account of " + loginMessage.getLoginName());
            }
            //send LoginResponse to the Client
            response.setUserConnectionInfo(loginMessage.getUserConnectionInfo());
            messageQueue.put(response);

        }

    }

    /**
     * Creates a message for a new Client, containing a list of all connected, 
     * logged in users.
     * @param info the ConnectionInfo of the client receiving the message
     * @return A TargetedServerMessage containing a String with a list of users
     */
    private TargetedServerMessage buildUserListMessage(UserConnectionInfo info){
        String userList = "Following users are logged in: \n";
        for(UserListeningThread userListeningThread : server.getNetworkListener().getUserListeningThreadList()){
            if(userListeningThread.getUserConnectionInfo().isLoggedIn()){
                userList += userListeningThread.getUserConnectionInfo().getUserAccountInfo().getDisplayName() + "\n";
            }
        }
        TargetedServerMessage targetedServerMessage = new TargetedServerMessage(userList);
        targetedServerMessage.setUserConnectionInfo(info);
        return targetedServerMessage;
    }
    /**
     * Sends a message to a specific target connected on this server
     * @param m the Message which should be sent to an user
     * @throws InterruptedException if the queue gets interrupted
     */
    public void sendToTarget(Message m) throws InterruptedException {
        switch (server.getMessageTypeDictionary().getType(m.getType())) {
            case TARGETTEXTMSG:
                //cast message
                TargetedTextMessage textMessage = (TargetedTextMessage) m;
                String receiver = textMessage.getReceiver();

                //Iterate through the list to find the receiver
                for (UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()) {
                    //Get the AccountInfo of the thread, find receiver of message
                    if (u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(receiver)) {
                        serializer.serialize(u.getUserConnectionInfo().getOut(), m);
                        return;
                    }
                }
                //If receiver wasnt found, he isnt online. Send message to sender that sending failed
                TargetedServerMessage serverMessage = new TargetedServerMessage("Failed to send message: " + ((TargetedTextMessage) m).getReceiver() + "is not online!");
                serverMessage.setUserConnectionInfo(m.getUserConnectionInfo());
                serializer.serialize(m.getUserConnectionInfo().getOut(), serverMessage);
                break;

            //because in case of a login process, we dont know the name of the client, so we get the connectioninfo of message
            case TARGETSERVERMSG:
            case LOGINRESPONSEMSG:
                serializer.serialize(m.getUserConnectionInfo().getOut(), m);
                break;
        }

    }

    /**
     * Check if the sender of this message has permission to send Messages to other users
     * @param m The message containing information of the sender in question
     */
    public void sendToAll(Message m) throws InterruptedException {
        for (UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()) {
            if (u.getUserConnectionInfo().isLoggedIn()) {
                serializer.serialize(u.getUserConnectionInfo().getOut(), m);
            }
        }
    }

    /**
     * Returns the MessageQueue
     * @return the MessageQueue of the MessageListener
     */
    public ArrayBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }
}
