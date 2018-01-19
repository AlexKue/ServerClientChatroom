package chatroom.server.room;

import chatroom.model.UserConnectionInfo;
import chatroom.model.message.RoomListMessage;
import chatroom.model.message.RoomMessage;
import chatroom.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class RoomHandler {
    private List<Room> roomList;
    private Server server;

    public RoomHandler(Server server) {
        roomList = new ArrayList<>();
        roomList.add(new Room("lobby"));
        this.server = server;
    }

    public void addRoom(String name) {
        //check if Room already exists
        for (Room r : roomList) {
            if (r.getName().equals(name)) {
                server.log(Level.WARNING, "RoomHandler: Room " + name + "already exists.");
                return;
            }
        }
        roomList.add(new Room(name));
        server.getBridge().updateRoomListView(getRoomNamesList());
        //Update the list of available rooms for all users
        try {
            server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
        } catch (InterruptedException e) {
            server.log(Level.WARNING, "RoomHandler: Exception while sending a RoomListMessage: ", e);
        }
        server.log(Level.INFO, "RoomHandler: Room\"" + name + "\" has been created.");
    }

    public void removeRoom(Room room) {
        if (room.getName().equals("lobby")) {
            server.log(Level.WARNING, "RoomHandler: Cannot remove room \"lobby\"");
        } else {
            roomList.remove(room);
            server.getBridge().updateRoomListView(getRoomNamesList());
            try {
                server.getMessageListener().getMessageQueue().put(buildRoomListMessage());
            } catch (InterruptedException e) {
                server.log(Level.WARNING, "RoomHandler: Exception while sending a RoomListMessage: ", e);
            }
            server.log(Level.INFO, "Room \"" + room.getName() + "\" has been removed");
        }
    }

    public void removeRoom(String name) {
        if (name.equals("lobby")) {
            server.log(Level.WARNING, "RoomHandler: Cannot remove room \"lobby\"");
        } else {
            for (Room r : roomList) {
                if (r.getName().equals(name)) {
                    roomList.remove(r);
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

    public boolean editRoom(String oldName, String newName) {
        if (oldName.equals("lobby")) {
            server.log(Level.WARNING, "RoomHandler: Cannot edit room \"lobby\"");
            return false;
        } else {
            Room room = getRoom(oldName);
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

    public Room getRoom(String name) {
        for (Room r : roomList) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public ArrayList<String> getRoomNamesList() {
        ArrayList<String> roomNames = new ArrayList<>();
        for (Room r : roomList) {
            roomNames.add(r.getName());
        }
        return roomNames;
    }

    public RoomListMessage buildRoomListMessage() {
        List<RoomMessage> list = new ArrayList<>();
        for (Room r : roomList) {
            list.add(new RoomMessage(r.getName(), r.getRoomSize()));
        }
        return new RoomListMessage(list);
    }
}
