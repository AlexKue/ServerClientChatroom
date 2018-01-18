package chatroom.server.gui;

import chatroom.server.Server;
import chatroom.server.ServerForTesting;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

public class Bridge {
    Server model;
    ServerGuiMain gui;

    public Bridge(Server server, ServerGuiMain serverGuiMain){
        this.model = server;
        this.gui = serverGuiMain;
    }

    //This Method is for Code to run when a Close is requested
    public void onClose() {
        model.log(Level.WARNING,"***** Shutting down Server *****");
        model.stop();
    }

    //Updates the room list view
    public void updateRoomListView(ArrayList<String> rooms){
        Platform.runLater(() -> gui.serverHomeGui.updateRoomListView(rooms));
    }

    public void updateUserListView(ArrayList<String> users){
        Platform.runLater(() -> gui.serverHomeGui.updateUserListView(users));
    }

    //Requests the room list from the Model TODO: ---ERSETZEN und IMPLEMETIEREN
    public ArrayList<String> requestRoomList(){
        return model.requestRoomList();
    }

    //Requests the room list from the model TODO: ---ERSETZEN und IMPLEMENTIEREN
    public ArrayList<String> requestUserList(){
        return model.requestUserList();
    }

    //After kicking a user the method updateUserListView has to be called, otherwise the View wont be updatet... TODO: ---ERSETZEN und IMPLEMENTIEREN
    public void kickUser(String user) {
        model.kickUser(user);
    }

    public void warnUser(String user) {
        model.warnUser(user);
    }

    //After baning a user the method updateUserListView has to be called, otherwise the View wont be updatet... TODO: ---ERSETZEN und IMPLEMENTIEREN
    public void banUser(String user) {
        model.banUser(user);
    }

    public void editRoom(String oldName, String newName){
        model.editRoom(oldName, newName);
    }

    public void addRoom(String name){
        model.addRoom(name);
    }

    public void deleteRoom(String name){
            model.deleteRoom(name);
    }

    //This is the Server Log
    public void addEventToLog(String event){
        gui.serverHomeGui.addEventToLog(event);
    }

    public ArrayList<String> getAllUsers() {
        return model.getAllUsers();
    }
}
