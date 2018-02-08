package chatroom.server.room;

import chatroom.model.UserConnectionInfo;
import chatroom.model.message.RoomListMessage;
import chatroom.model.message.RoomMessage;
import chatroom.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This class contains the list of all Rooms available for clients to join, and also provides methods to modify the list.
 * Clients will be notifies about changes.
 */
public class RoomHandler {
    private List<Room> publicRoomList;
    private Server server;


    public RoomHandler(Server server) {
        publicRoomList = new ArrayList<>();
        publicRoomList.add(new Room("lobby"));
        this.server = server;
    }

    /*
     * Methods handling public Rooms
     */

    /**
     * Creates a new Room Object and adds it to the list of Rooms, if it doesn't already exist.
     * Also updates the serverView and notifies all Clients about the change
     * @param name
     */
    public void addPublicRoom(String name) {
        //check if Room already exists
        for (Room r : publicRoomList) {
            if (r.getName().equals(name)) {
                server.log(Level.WARNING, "RoomHandler: Room " + name + "already exists.");
                return;
            }
        }
        publicRoomList.add(new Room(name));
        server.getBridge().updateRoomListView(getRoomNamesList());
        //Update the list of available rooms for all users
        try {
            server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
        } catch (InterruptedException e) {
            server.log(Level.WARNING, "RoomHandler: Exception while sending a RoomListMessage: ", e);
        }
        server.log(Level.INFO, "RoomHandler: Room\"" + name + "\" has been created.");
    }

    /**
     * Removes a room of the list, updates the serverView and notifies all clients about the change
     * @param room the Room as the object itself which should be removed from the list
     */
    public void removePublicRoom(Room room) {
        if (room.getName().equals("lobby")) {
            server.log(Level.WARNING, "RoomHandler: Cannot remove room \"lobby\"");
        } else {
            publicRoomList.remove(room);
            server.getBridge().updateRoomListView(getRoomNamesList());
            try {
                server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
            } catch (InterruptedException e) {
                server.log(Level.WARNING, "RoomHandler: Exception while sending a RoomListMessage: ", e);
            }
            server.log(Level.INFO, "Room \"" + room.getName() + "\" has been removed");
        }
    }

    /**
     * Removes a room of the list by looking up every room and comparing its name with the parameter. Also updates
     * serverView and notifies all clients about the change
     * @param name the name of the room which should be deleted
     */
    public void removePublicRoom(String name) {
        if (name.equals("lobby")) {
            server.log(Level.WARNING, "RoomHandler: Cannot remove room \"lobby\"");
        } else {
            for (Room r : publicRoomList) {
                if (r.getName().equals(name)) {
                    publicRoomList.remove(r);
                    server.getBridge().updateRoomListView(getRoomNamesList());
                    break;
                }
            }
            //Update the list of available rooms for all users
            try {
                server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
            } catch (InterruptedException e) {
                server.log(Level.WARNING, "RoomHandler: Exception while sending a RoomListMessage: ", e);
            }
            server.log(Level.INFO, "Room \"" + name + "\" has been removed");
        }
    }

    /**
     * Edits the name of the room and notifies all users about the change
     * @param oldName the old name of the room
     * @param newName the new name of the room
     * @return
     */
    public boolean editPublicRoom(String oldName, String newName) {
        if (oldName.equals("lobby")) {
            server.log(Level.WARNING, "RoomHandler: Cannot edit room \"lobby\"");
            return false;
        } else {
            Room room = getPublicRoom(oldName);
            room.setName(newName);
            server.getBridge().updateRoomListView(getRoomNamesList());
            //Update the list of available rooms for all users
            try {
                server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
            } catch (InterruptedException e) {
                server.log(Level.WARNING, "RoomHandler: Exception while sending a RoomListMessage: ", e);
            }
            server.log(Level.INFO, "RoomHandler: " + "Room \"" + oldName + "\" has been renamed to \"" + newName + "\"");
            return true;
        }
    }

    /**
     * Returns the room object with the specified name
     * @param name the name of the room object
     * @return the room with the specified name, or null if such room doesn't exist
     */
    public Room getPublicRoom(String name) {
        for (Room r : publicRoomList) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Returns a list of Rooms
     * @return the list of rooms on the server
     */
    public List<Room> getPublicRoomList() {
        return publicRoomList;
    }

    /**
     * A list of Strings with the names of existing rooms
     * @return a list of Strings with room names
     */
    public ArrayList<String> getRoomNamesList() {
        ArrayList<String> roomNames = new ArrayList<>();
        for (Room r : publicRoomList) {
            roomNames.add(r.getName());
        }
        return roomNames;
    }

    /**
     * Builds a RoomListMessage containing all existing rooms, so it can be sent to all clients, for updating
     * purposes
     * @return a RoomListMessage with information about all rooms
     */
    public RoomListMessage buildRoomListMessage() {
        List<RoomMessage> list = new ArrayList<>();
        for (Room r : publicRoomList) {
            list.add(new RoomMessage(r.getName(), r.getRoomSize()));
        }
        return new RoomListMessage(list);
    }
}
