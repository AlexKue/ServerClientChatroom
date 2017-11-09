package chatroom.server.listener;

import chatroom.model.*;
import chatroom.model.message.*;
import chatroom.serializer.Serializer;
import chatroom.server.Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageListener extends Thread {
    private final ArrayBlockingQueue<Message> messageQueue;
    private Server server;
    private final Serializer serializer;
    private UserStorage userStorage;

    public MessageListener(Server server) {
        messageQueue = new ArrayBlockingQueue<>(100);  //Synchronized queue containing messages from clients
        this.server = server;
        serializer = new Serializer();
        userStorage = new UserStorage();
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                Message m = messageQueue.poll(1, TimeUnit.SECONDS);
                if (m == null) {
                    continue;
                }
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
     * @throws InterruptedException
     */
    private void authenticate(Message m) throws InterruptedException {
        LoginMessage loginMessage = (LoginMessage) m;


        //check if username is registered on this server
        if (!userStorage.loginNameExists(loginMessage.getLoginName())) {

            //send Response Message
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
            return;

        } else {
            //case: username is registered
            //check if password is correct
            boolean success = userStorage.checkLogin(loginMessage.getLoginName(), loginMessage.getPassword());

            //check if login was successful, prepare ServerMessage
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
                UserAccountInfo accountInfo = userStorage.getUserAccountInfo(loginMessage.getLoginName());
                m.getUserConnectionInfo().setUserAccountInfo(accountInfo);
                System.out.println("User " + loginMessage.getLoginName() + " has logged in");
            } else {
                response = new LoginResponseMessage(LoginResponses.WRONG_PASSWORD);
                System.out.println("Someone failed to login into the account of " + loginMessage.getLoginName());
            }
            response.setUserConnectionInfo(loginMessage.getUserConnectionInfo());
            messageQueue.put(response);

        }

    }

    public void sendToTarget(Message m) {
        switch (server.getMessageTypeDictionary().getType(m.getType())) {
            case TARGETTEXTMSG:
                //Check, if user has permission to send something to the server
                if (!hasPermission(m)) {
                    return;
                }
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
     *
     * @param m The message containing information of the sender in question
     */
    private boolean hasPermission(Message m) {
        if (!m.getUserConnectionInfo().isLoggedIn()) {
            TargetedServerMessage serverMessage = new TargetedServerMessage("Rejected: You are not logged in! login with \"!login\" command!");
            serverMessage.setUserConnectionInfo(m.getUserConnectionInfo());
            serializer.serialize(m.getUserConnectionInfo().getOut(), serverMessage);
            return false;
        }
        return true;
    }

    public void sendToAll(Message m) {
        //Check, if user has permission to send something to the server
        if (!hasPermission(m)) {
            return;
        }
        for (UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()) {
            if (u.getUserConnectionInfo().isLoggedIn()) {
                serializer.serialize(u.getUserConnectionInfo().getOut(), m);
            }
        }
    }


    public ArrayBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }
}
