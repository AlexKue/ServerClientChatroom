package chatroom.server.listener;

import chatroom.model.*;
import chatroom.model.message.*;
import chatroom.model.message.Message;
import chatroom.serializer.Serializer;
import chatroom.server.Server;
import chatroom.server.room.Room;
import chatroom.server.room.RoomHandler;

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
                Server.logger.log(Level.INFO,"MessageListener: Next in Queue: " + messageTypeString);
                
                //Byte sent by client decides which type of message is sent
                switch (server.getMessageTypeDictionary().getType(m.getType())) {
                    case PUBLICSERVERMSG:
                        sendToAll(m);
                        break;
                    case PUBLICTEXTMSG:
                        sendToRoom(m,m.getUserConnectionInfo().getActiveRoom().getName());
                        break;
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
                }
                //Issues may occur on clients that message are sent too fast.
                //TODO: find more elegant fix, if there is any
                sleep(50);
            } catch (InterruptedException e) {
                Server.logger.log(Level.WARNING,"MessageListener interrupted by server!");
            } catch (IOException e) {
                Server.logger.log(Level.SEVERE,"Error while serializing!");
            }
        }
    }

    private void updateRoomUserLists(Message m, Room room) throws IOException {
        for(UserConnectionInfo info : room.getUserList()){
                serializer.serialize(info.getOut(), m);
        }
    }

    private void changeRoom(Message m) throws InterruptedException, IOException {
        RoomChangeRequestMessage message = (RoomChangeRequestMessage)m;
        Room activeRoom = m.getUserConnectionInfo().getActiveRoom();
        Room newRoom = server.getRoomHandler().getRoom(message.getRoomName());


        activeRoom.removeUser(m.getUserConnectionInfo());

        for(UserConnectionInfo info : activeRoom.getUserList()){
            TargetedServerMessage serverMessage = new TargetedServerMessage(message.getLoginName() +
                    "has gone to the Room \"" + message.getRoomName() + "\"");

            serverMessage.setUserConnectionInfo(info);
            serializer.serialize(info.getOut(),serverMessage);
        }

        newRoom.addUser(m.getUserConnectionInfo());
        m.getUserConnectionInfo().setActiveRoom(newRoom);
        Server.logger.log(Level.INFO, message.getLoginName() + "switched from Room \"" + activeRoom + "\" to \"" + newRoom + "\"");

        //Updating RoomUserLists for the 2 rooms
        List<String> oldRoomUserList = activeRoom.getUserNamelist();
        List<String> newRoomUserList = newRoom.getUserNamelist();
        RoomUserListMessage oldRoomList = new RoomUserListMessage(oldRoomUserList,activeRoom.getName());
        RoomUserListMessage newRoomList = new RoomUserListMessage(newRoomUserList,newRoom.getName());
        updateRoomUserLists(oldRoomList,activeRoom);
        updateRoomUserLists(newRoomList,newRoom);

        //Change Successfull
        serializer.serialize(m.getUserConnectionInfo().getOut(),new RoomChangeResponseMessage(true,newRoom.getName()));
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
                        Server.logger.log(Level.INFO, sender + " (to " + receiver + "): \"" + textMessage.getMessage() + "\" ");
                        return;
                    }
                }
                //If receiver was not found, he is not online. Send message to sender that sending failed
                TargetedServerMessage serverMessage = new TargetedServerMessage("Failed to send message: " + ((TargetedTextMessage) m).getReceiver() + "is not online!");
                Server.logger.log(Level.WARNING, "Failed to send message: " + ((TargetedTextMessage) m).getReceiver() + "is not online!");
                serverMessage.setUserConnectionInfo(m.getUserConnectionInfo());
                serializer.serialize(m.getUserConnectionInfo().getOut(), serverMessage);
                break;

            //because in case of a login process, we don't know the name of the client, so we get the connectionInfo of message
            case TARGETSERVERMSG:
                String name = m.getUserConnectionInfo().getUserAccountInfo().getLoginName();
                String content = ((TargetedServerMessage)m).getMessage();
                Server.logger.log(Level.INFO, "Servermessage to " + name + ": " + content);
                serializer.serialize(m.getUserConnectionInfo().getOut(), m);
                break;
            case LOGINRESPONSEMSG:
                LoginResponseMessage response = ((LoginResponseMessage)m);
                Server.logger.log(Level.INFO,"Login response for " + m.getUserConnectionInfo().getSocket().getInetAddress() +
                        ": " + response.getResponse().toString());
                serializer.serialize(m.getUserConnectionInfo().getOut(), m);
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

    private void sendToRoom(Message m, String roomName) throws IOException {
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

        //Fetch List of Names in Room of the client the user was before disconnecting
        List<String> newRoomUserList = activeRoom.getUserNamelist();

        //Build the message
        RoomUserListMessage roomUserListMessage = new RoomUserListMessage(newRoomUserList,activeRoom.getName());

        //Update the lists
        updateRoomUserLists(roomUserListMessage,activeRoom);
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

        //Look up if loginName is already in Use

        //CASE: Username not taken
        if (!userStorage.loginNameExists(loginMessage.getLoginName())) {

            //Prepare ResponseMessage: No one uses this name
            LoginResponseMessage responseMessage = new LoginResponseMessage(LoginResponses.CREATED_ACCOUNT);
            responseMessage.setUserConnectionInfo(m.getUserConnectionInfo());

            //add account info to connection info
            userStorage.addClient(loginMessage.getLoginName(), loginMessage.getPassword());
            userStorage.saveUserDataToFile();

            UserAccountInfo userInfo = userStorage.getUserAccountInfo(loginMessage.getLoginName());
            m.getUserConnectionInfo().setUserAccountInfo(userInfo);

            //set User as logged in
            m.getUserConnectionInfo().setLoggedIn(true);

            //Add him to the lobby
            m.getUserConnectionInfo().setActiveRoom(server.getRoomHandler().getRoom("lobby"));
            server.getRoomHandler().getRoom("lobby").addUser(m.getUserConnectionInfo());

            //Prepare List of available rooms
            List<RoomMessage> roomMessageList = new ArrayList<>();
            for(Room r : server.getRoomHandler().getRoomList()){
                roomMessageList.add(new RoomMessage(r.getName(),r.getRoomSize()));
            }
            RoomListMessage roomListMessage = new RoomListMessage(roomMessageList);

            //Prepare List of Users in the lobby
            List<String> userNameList = server.getRoomHandler().getRoom("lobby").getUserNamelist();
            RoomUserListMessage roomUserListMessage = new RoomUserListMessage(userNameList,"lobby");

            ServerUserListMessage serverUserListMessage= buildUserListMessage();

            //send Messages
            sendToTarget(responseMessage);
            serializer.serialize(m.getUserConnectionInfo().getOut(), roomUserListMessage);
            Server.logger.log(Level.INFO,"roomUserListMessage");
            serializer.serialize(m.getUserConnectionInfo().getOut(), roomListMessage);
            Server.logger.log(Level.INFO,"roomListMessage");
            serializer.serialize(m.getUserConnectionInfo().getOut(), serverUserListMessage);
            Server.logger.log(Level.INFO,"serverUserListMessage");


            Server.logger.log(Level.INFO, "Created new Account for " + loginMessage.getLoginName() + "@" + loginMessage.getUserConnectionInfo().getSocket().getInetAddress());

//            //Notify other users that someone connected
//            sendToRoom(new PublicServerMessage(userInfo.getDisplayName() + " has connected to the Server!"),"lobby");
//
//            //Update lists of all users
//            sendToAll(buildUserListMessage());
//
//            //Fetch List of Names in the lobby
//            List<String> newRoomUserList = server.getRoomHandler().getRoom("lobby").getUserNamelist();
//
//            //Build the message
//            RoomUserListMessage newRoomUserListMessage = new RoomUserListMessage(newRoomUserList,"lobby");
//
//            //Update the lists
//            updateRoomUserLists(newRoomUserListMessage,server.getRoomHandler().getRoom("lobby"));
            return;

        } else {

            //Now we know that the loginName is already in user. In case that the password is correct,
            //check, if another user is already using this account.
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

                        System.out.println("A client tried to log into the Account of "+loginMessage.getLoginName()+" while it was already online!");
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

                //Prepare Messages for new user
                List<RoomMessage> roomMessageList = new ArrayList<>();
                for(Room r : server.getRoomHandler().getRoomList()){
                    roomMessageList.add(new RoomMessage(r.getName(),r.getRoomSize()));
                }
                RoomListMessage roomListMessage = new RoomListMessage(roomMessageList);

                List<String> userNameList = server.getRoomHandler().getRoom("lobby").getUserNamelist();
                RoomUserListMessage roomUserListMessage = new RoomUserListMessage(userNameList,"lobby");

                ServerUserListMessage serverUserListMessage= buildUserListMessage();

                //send the messages
                sendToTarget(response);
                serializer.serialize(m.getUserConnectionInfo().getOut(), roomUserListMessage);
                serializer.serialize(m.getUserConnectionInfo().getOut(), roomListMessage);
                serializer.serialize(m.getUserConnectionInfo().getOut(), serverUserListMessage);


                //Notify other users that someone connected
                sendToRoom(new PublicServerMessage(accountInfo.getDisplayName() + " has connected to the Server!"),"lobby");
                updateRoomUserLists(roomUserListMessage,server.getRoomHandler().getRoom("lobby"));

                Server.logger.log(Level.INFO,"User " + loginMessage.getLoginName() + " has logged in!");

            } else {
                //We know that the password is wrong. Notify user.
                response = new LoginResponseMessage(LoginResponses.WRONG_PASSWORD);
                Server.logger.log(Level.INFO,m.getUserConnectionInfo().getSocket().getInetAddress() + " failed to login into the account of " + loginMessage.getLoginName());
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


}
