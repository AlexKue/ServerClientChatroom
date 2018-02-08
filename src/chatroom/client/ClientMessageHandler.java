package chatroom.client;

import chatroom.model.message.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class
ClientMessageHandler extends Thread {
    private Client client;

    public ClientMessageHandler(Client client) {
        this.client = client;
    }

    @Override
    public void run(){
        while (client.isRunning()){
            try {
                Message m = client.getClientListener().getMessageQueue().poll(1, TimeUnit.SECONDS);

                if(m == null){
                    continue;
                }
                handleMessage(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.err.println("Shutting down Message Handler!");
    }

    public void handleMessage(Message message) {
        switch (client.getMessageTypeDictionary().getType(message.getType())) {
            case PUBLICTEXTMSG:
                PublicTextMessage publicTextMessage = ((PublicTextMessage) message);
                client.getBridge().AddMessageToView(publicTextMessage.getSender(), publicTextMessage.getMessage());
                String publicString = publicTextMessage.getSender() + ": " + publicTextMessage.getMessage();
                System.out.println(publicString);
                break;
            case PUBLICSERVERMSG:
                PublicServerMessage publicServerMessage = ((PublicServerMessage) message);
                client.getBridge().AddMessageToView("SERVER", publicServerMessage.getMessage());
                System.out.println("*** " + publicServerMessage.getMessage() + " ***");
                break;
            case TARGETSERVERMSG:
                TargetedServerMessage targetedServerMessage = ((TargetedServerMessage) message);
                client.getBridge().AddMessageToView("SERVER", targetedServerMessage.getMessage());
                System.out.println("Server: " + targetedServerMessage.getMessage());
                break;
            case TARGETTEXTMSG:
                TargetedTextMessage targetedTextMessage = ((TargetedTextMessage) message);
                client.getBridge().addPrivateMessage(targetedTextMessage.getSender(),targetedTextMessage.getMessage(),false);
                break;
            case WARNINGMSG:
                WarningMessage warningMessage = ((WarningMessage) message);
                switch (warningMessage.getSeverity()){
                    case 0:
                        client.getBridge().issueBox(warningMessage.getMessage(),false);
                        break;
                    case 1:
                    case 2:
                        client.getBridge().issueBox(warningMessage.getMessage(),true);
                        break;
                }
                break;
            case ROOMLISTMSG:
                RoomListMessage roomListMessage = ((RoomListMessage) message);
                client.setRoomMessageList(roomListMessage.getRoomList());
                client.getBridge().onRoomUpdate(client.getRooms());
                break;
            case ROOMCHANGERESPONSEMSG:
                RoomChangeResponseMessage roomChangeResponseMessage = ((RoomChangeResponseMessage) message);
                client.setActiveRoom(roomChangeResponseMessage.getRoomName());
                client.getBridge().onRoomChangeRequestAccepted(roomChangeResponseMessage.getRoomName());
                if (roomChangeResponseMessage.isSuccessful()) {
                    client.getBridge().AddMessageToView("SERVER", "You are now talking in Room\"" + roomChangeResponseMessage.getRoomName() + "\"");
                }
                break;
            case SERVERUSERLISTMSG:
                ServerUserListMessage serverUserListMessage = ((ServerUserListMessage) message);
                client.setServerUserList(serverUserListMessage.getServerUserList());
                client.getBridge().allUsersUpdate((ArrayList<String>) client.getAllUsers());
                break;
            case ROOMUSERLISTMSG:
                RoomUserListMessage roomUserListMessage = ((RoomUserListMessage) message);
                client.setRoomUserList((ArrayList<String>) roomUserListMessage.getUserList());
                client.getBridge().userRoomUpdate((ArrayList<String>) roomUserListMessage.getUserList());
                break;
            case ROOMNAMEEDITMSG:
                RoomNameEditMessage roomNameEditMessage = ((RoomNameEditMessage)message);
                client.setActiveRoom(roomNameEditMessage.getNewName());
                client.getBridge().onRoomChangeRequestAccepted(roomNameEditMessage.getNewName());
                break;
            case PRIVATEROOMSTARTREQMSG:
                PrivateChatStartRequestMessage privateChatStartRequestMessage = ((PrivateChatStartRequestMessage)message);
                client.getBridge().processStartRequestForPrivateChat(privateChatStartRequestMessage.getRequester());
                break;
            case PRIVATEROOMENDREQMSG:
                PrivateChatEndRequestMessage privateChatEndRequestMessage = ((PrivateChatEndRequestMessage)message);
                client.getBridge().changePrivateChatActiveStatus(privateChatEndRequestMessage.getRequester());
                client.getBridge().addPrivateMessage(privateChatEndRequestMessage.getRequester(), privateChatEndRequestMessage.getRequester()+" has disconected from chat!", true);
                break;
            case LOGINRESPONSEMSG:
                client.getBridge().onServerLoginAnswer(((LoginResponseMessage) message).getResponse());
                switch (((LoginResponseMessage) message).getResponse()) {
                    case SUCCESS:
                        client.setLoggedIn(true);
                        System.out.println("*** You are logged in! ***");
                        break;
                    case CREATED_ACCOUNT:
                        client.setLoggedIn(true);
                        System.out.println("*** This name was not given. Created a new Account! ***");
                        break;
                    case ALREADY_LOGGED_IN:
                        System.out.println("*** Someone is already using your Account!!!! ***");
                        System.out.println("*** Your Account might be in danger. Contact an admin! ***");
                        break;
                    case WRONG_PASSWORD:
                        System.out.println("*** Wrong password! Please try again! ***");
                        break;
                }
                break;

        }


    }
}
