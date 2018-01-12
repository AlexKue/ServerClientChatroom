package chatroom.client;

import chatroom.model.message.*;
import chatroom.serializer.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Deserialize data from Stream and puts it in a Message Object,
 * displaying them afterwards
 */
public class ClientListeningThread extends Thread {
    private Client client;
    private InputStream in;
    private Serializer serializer;

    public ClientListeningThread(InputStream in, Client client) throws IOException {
        this.in = in;
        serializer = new Serializer();
        this.client = client;
    }


    @Override
    public void run() {
        while (client.isRunning()) {
            try {
                //Read byte from stream to decide on which type of message is incoming
                byte type = (byte)in.read();

                //Socket is closed if the Stream returns -1
                if(type == (byte)-1){
                    throw new IOException("Cannot reach server");
                }
                //deserialize message from stream and put it into an message
                Message m = serializer.deserialize(in, type);

                handleMessage(m);
            } catch (IOException ex) {
                System.err.println("Connection Lost: " + ex.toString() + "\nShutting down client!");
                client.stop();
            }
        }
        System.err.println("Shutting down receiving handler!");
    }

    /**
     * Prints the message incoming from the server and does further actions if needed
     * @param message The message which should be handled
     */
    private void handleMessage(Message message) {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (client.getMessageTypeDictionary().getType(message.getType())) {
            case PUBLICTEXTMSG:
                PublicTextMessage publicTextMessage = ((PublicTextMessage) message);
                client.getBridge().AddMessageToView(publicTextMessage.getSender(),publicTextMessage.getMessage());
                String publicString = publicTextMessage.getSender() + ": " + publicTextMessage.getMessage();
                System.out.println(publicString);
                break;
            case PUBLICSERVERMSG:
                PublicServerMessage publicServerMessage = ((PublicServerMessage) message);
                client.getBridge().AddMessageToView("SERVER",publicServerMessage.getMessage());
                System.out.println("*** " + publicServerMessage.getMessage() + " ***");
                break;
            case TARGETSERVERMSG:
                TargetedServerMessage targetedServerMessage = ((TargetedServerMessage) message);
                client.getBridge().AddMessageToView("SERVER",targetedServerMessage.getMessage());
                System.out.println("Server: " + targetedServerMessage.getMessage());
                break;
            case TARGETTEXTMSG:
                TargetedTextMessage targetedTextMessage = ((TargetedTextMessage) message);
                String targetedString = targetedTextMessage.getSender() + " (whispered): " + targetedTextMessage.getMessage();
                System.out.println(targetedString);
                break;
            case WARNINGMSG:
                WarningMessage warningMessage = ((WarningMessage)message);
                client.getBridge().issueBox(warningMessage.getMessage());
                break;
            case ROOMLISTMSG:
                RoomListMessage roomListMessage = ((RoomListMessage)message);
                client.setRoomMessageList(roomListMessage.getRoomList());
                client.getBridge().onRoomUpdate(client.getRooms());
                break;
            case ROOMCHANGERESPONSEMSG:
                RoomChangeResponseMessage roomChangeResponseMessage= ((RoomChangeResponseMessage)message);
                if(roomChangeResponseMessage.isSuccessful()){
                    client.setActiveRoom(roomChangeResponseMessage.getRoomName());
                    client.getBridge().onRoomChangeRequestAccepted(roomChangeResponseMessage.getRoomName());
                }
                break;
            case SERVERUSERLISTMSG:
                ServerUserListMessage serverUserListMessage = ((ServerUserListMessage)message);
                client.setServerUserList(serverUserListMessage.getServerUserList());
                client.getBridge().allUsersUpdate((ArrayList<String>) client.getAllUsers());
                break;
            case ROOMUSERLISTMSG:
                RoomUserListMessage roomUserListMessage = ((RoomUserListMessage)message);
                client.setRoomUserList((ArrayList<String>) roomUserListMessage.getUserList());
                client.getBridge().userRoomUpdate((ArrayList<String>) roomUserListMessage.getUserList());
            case LOGINRESPONSEMSG:
                client.getBridge().onServerLoginAnswer(((LoginResponseMessage)message).getResponse());
                switch (((LoginResponseMessage)message).getResponse()){
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
                        client.stop();
                        break;
                    case WRONG_PASSWORD:
                        System.out.println("*** Wrong password! Please try again! ***");
                        //client.getClientSender().authenticate();
                        break;
                } break;

        }

    }

}
