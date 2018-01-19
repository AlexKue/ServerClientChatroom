package chatroom.server.listener;

import chatroom.model.*;
import chatroom.model.message.*;
import chatroom.model.message.Message;
import chatroom.serializer.Serializer;
import chatroom.server.Server;
import chatroom.server.room.Room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                //If Queue is empty, wait for a new Message for at most 1 second, so that we can check
                //regularly if the server is still running
                Message m = messageQueue.poll(1, TimeUnit.SECONDS);
                if (m == null) {
                    continue;
                }

                //Display on server what it is working: 
                String messageTypeString = server.getMessageTypeDictionary().getType(m.getType()).toString();               
                server.log(Level.INFO,"MessageListener: Next in Queue: " + messageTypeString);
                
                //Byte sent by client decides which type of message is sent
                switch (server.getMessageTypeDictionary().getType(m.getType())) {
                    case PUBLICSERVERMSG:
                    case ROOMLISTMSG:
                        sendToAll(m);
                        break;
                    case PUBLICTEXTMSG:
                        sendToRoom(m,m.getUserConnectionInfo().getActiveRoom().getName());
                        break;
                    case ROOMCHANGERESPONSEMSG:
                    case TARGETTEXTMSG:
                    case TARGETSERVERMSG:
                    case LOGINRESPONSEMSG:
                        sendToTarget(m);
                        break;
                    case LOGINMSG:
                        authenticate(m);
                        break;
                    case LOGOUTMSG:
                        logOut(m);
                        break;
                    case ROOMCHANGEREQMSG:
                        changeRoom(m);
                        break;
                    case ROOMNAMEEDITMSG:
                        sendToRoom(m,((RoomNameEditMessage)m).getNewName());
                        break;
                    case WARNINGMSG:
                        warnUser(m);
                        break;
                    case ROOMUSERLISTMSG:
                        sendToRoom(m,((RoomUserListMessage)m).getRoomName());
                        break;
                }
                //Issues may occur on clients that message are sent too fast.
                //TODO: find more elegant fix, if there is any
                sleep(50);
            } catch (InterruptedException e) {
                server.log(Level.WARNING,"MessageListener: Interrupted by server!");
            } catch (IOException e) {
                server.log(Level.SEVERE,"MessageListener: Error while serializing!", e);
            }
        }
    }

    /**
     * Sends a WarningMessage, popping up a window at the client's screen and cutting the connection off, depending of
     * severity
     * @param m the WarningMessage
     * @throws IOException
     */
    private void warnUser(Message m) throws IOException {
        serializer.serialize(m.getUserConnectionInfo().getOut(),m);
        switch (((WarningMessage)m).getSeverity()){
            case 2: userStorage.banUser(m.getUserConnectionInfo().getUserAccountInfo());
            case 1: server.getNetworkListener().removeClient(m.getUserConnectionInfo().getUserAccountInfo().getLoginName());
        }
    }

    /**
     * Sets the active Room of the client sending the request to the new Room, removes the user from the old room,
     * adds him to the new one and updates userlists of the users of both rooms.
     * @param m a RoomChangeRequestMessage of the user requesting a room change
     * @throws InterruptedException
     * @throws IOException
     */
    private void changeRoom(Message m) throws InterruptedException, IOException {
        RoomChangeRequestMessage message = (RoomChangeRequestMessage)m;
        Room oldRoom = m.getUserConnectionInfo().getActiveRoom();
        Room newRoom = server.getRoomHandler().getRoom(message.getRoomName());

        if(oldRoom.getName().equals(newRoom.getName())){
            RoomChangeResponseMessage responseMessage = new RoomChangeResponseMessage(false,oldRoom.getName());
            responseMessage.setUserConnectionInfo(m.getUserConnectionInfo());
            messageQueue.put(responseMessage);
            return;
        }

        //Removing the User from the old room
        oldRoom.removeUser(m.getUserConnectionInfo());
        PublicServerMessage serverMessageOldRoom = new PublicServerMessage(message.getLoginName() +
                " has gone to the Room \"" + message.getRoomName() + "\"");
        sendToRoom(serverMessageOldRoom,oldRoom.getName());
        sleep(50);

        //Updates userlist of old room
        List<String> oldRoomUserList = oldRoom.getUserNameList();
        RoomUserListMessage oldRoomList = new RoomUserListMessage(oldRoomUserList,oldRoom.getName());
        sendToRoom(oldRoomList,oldRoom.getName());
        sleep(50);

        //Notify users about change
        PublicServerMessage serverMessageNewRoom = new PublicServerMessage(message.getLoginName() +
                " has joined your Room.");
        sendToRoom(serverMessageNewRoom,newRoom.getName());
        sleep(50);

        //Adding the user to the new Room
        m.getUserConnectionInfo().setActiveRoom(newRoom);
        newRoom.addUser(m.getUserConnectionInfo());

        //Updating RoomUserLists of the new Room
        List<String> newRoomUserList = newRoom.getUserNameList();
        RoomUserListMessage newRoomListMessage = new RoomUserListMessage(newRoomUserList,newRoom.getName());
        sendToRoom(newRoomListMessage,newRoom.getName());
        sleep(50);

        //Change Successful
        RoomChangeResponseMessage responseMessage = new RoomChangeResponseMessage(true, newRoom.getName());
        responseMessage.setUserConnectionInfo(m.getUserConnectionInfo());
        messageQueue.put(responseMessage);

        //updating the Serverlistview
        server.getBridge().updateUserListView(server.getUserListWithRooms());
        server.log(Level.INFO, message.getLoginName() + " switched from Room \"" + oldRoom.getName() + "\" to \"" + newRoom.getName() + "\"");
    }

    /**
     * Sends a message to a specific target connected on this server.
     * This method handles differently based on message received
     * @param m the Message which should be sent to an user
     * @throws InterruptedException if the queue gets interrupted
     */
    private void sendToTarget(Message m) throws InterruptedException, IOException {
        switch (server.getMessageTypeDictionary().getType(m.getType())) {
            case TARGETTEXTMSG:
                //cast message
                TargetedTextMessage textMessage = (TargetedTextMessage) m;
                String receiver = textMessage.getReceiver();
                String sender = textMessage.getUserConnectionInfo().getUserAccountInfo().getDisplayName();

                //Iterate through the list to find the receiver
                for (UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()) {
                    //Get the AccountInfo of the thread, find receiver of message
                    if (u != null && u.getUserConnectionInfo().getUserAccountInfo().getLoginName().equals(receiver)) {
                        serializer.serialize(u.getUserConnectionInfo().getOut(), m);
                        server.log(Level.INFO, sender + " (to " + receiver + "): \"" + textMessage.getMessage() + "\" ");
                        return;
                    }
                }
                //If receiver was not found, he is not online. Send message to sender that sending failed
                TargetedServerMessage serverMessage = new TargetedServerMessage("Failed to send message: " + ((TargetedTextMessage) m).getReceiver() + "is not online!");
                server.log(Level.WARNING, "Failed to send message: " + ((TargetedTextMessage) m).getReceiver() + "is not online!");
                serverMessage.setUserConnectionInfo(m.getUserConnectionInfo());
                serializer.serialize(m.getUserConnectionInfo().getOut(), serverMessage);
                break;

            //because in case of a login process, we don't know the name of the client, so we get the connectionInfo of message
            case TARGETSERVERMSG:
                String name = m.getUserConnectionInfo().getUserAccountInfo().getLoginName();
                String content = ((TargetedServerMessage)m).getMessage();
                server.log(Level.INFO, "Sending: Servermessage to " + name + ": " + content);
                serializer.serialize(m.getUserConnectionInfo().getOut(), m);
                break;
            case LOGINRESPONSEMSG:
                LoginResponseMessage response = ((LoginResponseMessage)m);
                server.log(Level.INFO,"Sending: LoginResponse for " + m.getUserConnectionInfo().getSocket().getInetAddress() +
                        ": " + response.getResponse().toString());
                serializer.serialize(m.getUserConnectionInfo().getOut(), m);
                break;
            case ROOMCHANGERESPONSEMSG:
                serializer.serialize(m.getUserConnectionInfo().getOut(),m);
                server.log(Level.INFO,"Sending: RoomChangeResponse for " + m.getUserConnectionInfo().getUserAccountInfo().getLoginName());
                break;
        }
    }


    /**
     * Sends a message to all currently loggedIn users
     * @param m The message being sent to all clients
     */
    private void sendToAll(Message m) throws InterruptedException, IOException {
        //Go trough the list of Threads and send to loggedIn users
        for (UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()) {
            if (u != null && u.getUserConnectionInfo().isLoggedIn()) {
                serializer.serialize(u.getUserConnectionInfo().getOut(), m);
            }
        }
    }

    /**
     * Sends a message to all Users in the room
     * @param m the message being sent to the room
     * @param roomName the room the message should be sent to
     * @throws IOException
     */
    private void sendToRoom(Message m, String roomName) throws IOException {
        String logmsg = "Sending: [" + server.getMessageTypeDictionary().getType(m.getType()).toString() + "] ";
        switch (server.getMessageTypeDictionary().getType(m.getType())) {
            case PUBLICTEXTMSG:
                PublicTextMessage publicTextMessage = (PublicTextMessage)m;
                logmsg = logmsg.concat(publicTextMessage.getSender()+ "@" + roomName + ": " + publicTextMessage.getMessage());
                break;
            case PUBLICSERVERMSG:
                PublicServerMessage publicServerMessage = (PublicServerMessage)m;
                logmsg = logmsg.concat("SERVER@" + roomName + ": " + publicServerMessage.getMessage());
                break;
            case ROOMUSERLISTMSG:
                logmsg = logmsg.concat("Updating RoomUserLists for Clients @" + roomName);
                break;
        }
        server.log(Level.INFO,logmsg);

        Room room = server.getRoomHandler().getRoom(roomName);
        for(UserConnectionInfo info : room.getUserList()){
            serializer.serialize(info.getOut(),m);
        }
    }

    /**
     * Sends a message to all clients that a user has disconnected and removes the client
     * wishing to disconnect from the ThreadList of the server
     * @param m the LogoutMessage from the Client
     * @throws InterruptedException if an Interrupt occurs
     */
    private void logOut(Message m) throws InterruptedException, IOException {
        String username = m.getUserConnectionInfo().getUserAccountInfo().getDisplayName();
        Room activeRoom = m.getUserConnectionInfo().getActiveRoom();
        server.getNetworkListener().removeClient(username);

        //update ServerUserList
        sendToAll(buildUserListMessage());

        //Fetch List of Names in Room of the ServerUserclient the user was before disconnecting
        List<String> newRoomUserList = activeRoom.getUserNameList();

        //Build the message
        RoomUserListMessage roomUserListMessage = new RoomUserListMessage(newRoomUserList,activeRoom.getName());

        //Update the lists
        sendToRoom(roomUserListMessage,activeRoom.getName());

        server.getBridge().updateUserListView(server.getUserListWithRooms());

    }

    /**
     * Checks the login data of an user and sends <code>LoginResponseMessages</code>
     * back to the user
     *
     * @param m the message containing loginName and password
     * @throws InterruptedException if the queue gets interrupted
     */
    private void authenticate(Message m) throws InterruptedException, IOException {
        LoginMessage loginMessage = (LoginMessage) m;

        //First check if user is banned
        if(userStorage.isBanned(loginMessage.getLoginName())){
            LoginResponseMessage response = new LoginResponseMessage(LoginResponses.BANNED);
            response.setUserConnectionInfo(m.getUserConnectionInfo());
            sendToTarget(response);
            return;
        }

        //Look up if loginName is already in Use
        //CASE: Username not taken
        if (!userStorage.loginNameExists(loginMessage.getLoginName())) {

            //add account info to connection info
            userStorage.addClient(loginMessage.getLoginName(), loginMessage.getPassword());
            userStorage.saveUserDataToFile();
            UserAccountInfo userInfo = userStorage.getUserAccountInfo(loginMessage.getLoginName());
            m.getUserConnectionInfo().setUserAccountInfo(userInfo);

            //Prepare ResponseMessage: No one uses this name
            LoginResponseMessage responseMessage = new LoginResponseMessage(LoginResponses.CREATED_ACCOUNT);
            responseMessage.setUserConnectionInfo(m.getUserConnectionInfo());

            //set User as logged in
            m.getUserConnectionInfo().setLoggedIn(true);

            //Add him to the lobby
            m.getUserConnectionInfo().setActiveRoom(server.getRoomHandler().getRoom("lobby"));
            server.getRoomHandler().getRoom("lobby").addUser(m.getUserConnectionInfo());

            //Prepare List of available rooms
            RoomListMessage roomListMessage = server.getRoomHandler().buildRoomListMessage();

            //Prepare List of Users in the lobby
            List<String> roomUserNameList = server.getRoomHandler().getRoom("lobby").getUserNameList();
            RoomUserListMessage roomUserListMessage = new RoomUserListMessage(roomUserNameList,"lobby");
            ServerUserListMessage serverUserListMessage = buildUserListMessage();

            //Send user a message that he has logged into the lobby
            Message roomChangeResponse = new RoomChangeResponseMessage(true, "lobby");
            roomChangeResponse.setUserConnectionInfo(m.getUserConnectionInfo());

            //send Messages
            //send the authentication response
            sendToTarget(responseMessage);
            sleep(50);

            //send the list of available rooms to the new Client
            serializer.serialize(m.getUserConnectionInfo().getOut(), roomListMessage);
            sleep(50);

            //update the list of users logged in in the lobby for all users in the lobby
            sendToRoom(roomUserListMessage,"lobby");
            sleep(50);

            //update lists of all users in the server for all clients
            sendToAll(serverUserListMessage);

            sleep(50);
            //send room change response
            sendToTarget(roomChangeResponse);

            server.log(Level.INFO, "Created new Account for " + loginMessage.getLoginName() + "@" + loginMessage.getUserConnectionInfo().getSocket().getInetAddress());
            sendToRoom(new PublicServerMessage(m.getUserConnectionInfo().getUserAccountInfo().getDisplayName() + " has connected to the Server!"),"lobby");
            server.getBridge().updateUserListView(server.getUserListWithRooms());
            return;
            //CASE: Username is taken
        } else {
            //Now we know that the loginName is already in user. In case that the password is correct,
            //check, if another user is already using this account.
            boolean success = userStorage.checkLogin(loginMessage.getLoginName(), loginMessage.getPassword());
            LoginResponseMessage response;

            //CASE: Correct password
            if (success) {
                //check if another user is already using the account
                for (UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()) {
                    UserConnectionInfo userInfo = u.getUserConnectionInfo();

                    //check in the list if an user has already taken the name
                    if (userInfo.isLoggedIn() && userInfo.getUserAccountInfo().getLoginName().equals(loginMessage.getLoginName())) {
                        LoginResponseMessage message = new LoginResponseMessage(LoginResponses.ALREADY_LOGGED_IN);
                        message.setUserConnectionInfo(m.getUserConnectionInfo());
                        messageQueue.put(message);
                        server.log(Level.WARNING,"A client tried to log into the Account of "+loginMessage.getLoginName()+" while it was already online!");
                        return;
                    }
                }

                //No one else uses this account, grant access
                response = new LoginResponseMessage(LoginResponses.SUCCESS);
                response.setUserConnectionInfo(loginMessage.getUserConnectionInfo());

                m.getUserConnectionInfo().setLoggedIn(true);

                //set AccountInfo for the connection
                UserAccountInfo accountInfo = userStorage.getUserAccountInfo(loginMessage.getLoginName());
                m.getUserConnectionInfo().setUserAccountInfo(accountInfo);

                //Set Room to lobby
                m.getUserConnectionInfo().setActiveRoom(server.getRoomHandler().getRoom("lobby"));
                server.getRoomHandler().getRoom("lobby").addUser(m.getUserConnectionInfo());

                //Prepare List of available rooms
                RoomListMessage roomListMessage = server.getRoomHandler().buildRoomListMessage();

                List<String> userNameList = server.getRoomHandler().getRoom("lobby").getUserNameList();
                RoomUserListMessage roomUserListMessage = new RoomUserListMessage(userNameList,"lobby");
                ServerUserListMessage serverUserListMessage = buildUserListMessage();

                //Send user a message that he has logged into the lobby
                Message roomChangeResponse = new RoomChangeResponseMessage(true, "lobby");
                roomChangeResponse.setUserConnectionInfo(m.getUserConnectionInfo());

                //send the messages
                //send authenticate response
                sendToTarget(response);
                sleep(50);

                //send list of available rooms to the client
                serializer.serialize(m.getUserConnectionInfo().getOut(), roomUserListMessage);
                sleep(50);

                //update lists of users in the lobby for all users in the lobby
                sendToRoom(roomListMessage,"lobby");
                sleep(50);

                //update lists of all users connected on the server for everyone
                sendToAll(serverUserListMessage);

                //send room change response to the client
                sendToTarget(roomChangeResponse);

                //Notify other users that someone connected
                sendToRoom(new PublicServerMessage(accountInfo.getDisplayName() + " has connected to the Server!"),"lobby");
                server.getBridge().updateUserListView(server.getUserListWithRooms());

                server.log(Level.INFO,"User " + loginMessage.getLoginName() + " has logged in!");
            } else {
                //We know that the password is wrong. Notify user.
                response = new LoginResponseMessage(LoginResponses.WRONG_PASSWORD);
                server.log(Level.INFO,m.getUserConnectionInfo().getSocket().getInetAddress() + " failed to login into the account of " + loginMessage.getLoginName());
                response.setUserConnectionInfo(m.getUserConnectionInfo());
                sendToTarget(response);
            }
        }
    }

    /**
     * Creates a List of names of logged in users
     * @return A ServerUserListMessage containing a List of usernames
     */
    private ServerUserListMessage buildUserListMessage(){
        List<String> userList = new ArrayList<>();
        for(UserListeningThread userListeningThread : server.getNetworkListener().getUserListeningThreadList()){
            if(userListeningThread.getUserConnectionInfo().isLoggedIn()){
                userList.add(userListeningThread.getUserConnectionInfo().getUserAccountInfo().getLoginName());
            }
        }
        ServerUserListMessage m = new ServerUserListMessage(userList);
        return m;
    }

    /**
     * Returns the MessageQueue
     * @return the MessageQueue of the MessageListener
     */
    public ArrayBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

    /**
     * Return the UserStorage of the server containing passwords, the banlist etc.
     * @return the UserStorage of the server
     */
    public UserStorage getUserStorage() {
        return userStorage;
    }
}
