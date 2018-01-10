package chatroom.ClientGui;

import chatroom.client.ClientForTesting;

import java.util.ArrayList;

public class Bridge {
    ClientForTesting model;
    GuiMain gui;

    public Bridge(ClientForTesting client, GuiMain guiMain){
        this.model = client;
        this.gui = guiMain;
    }


    //called by the gui to send the login data
    public void sendLoginData(String username, String password){
        model.login(username, password);
    }

    //called by the model to let the gui know whether the login succeeded or not
    public void onServerLoginAnswer(boolean answer){
        gui.onLoginAnswer(answer);
    }

    public void SendMessage(String message){
        model.sendMessage(message);
    }

    public void AddMessageToView(String username, String message){
        gui.AddMessage(username, message);
    }
    public String getUsername(){
      return model.getUsername();
    }
    public ArrayList<String[]> getRooms(){
        return model.getRooms();
    }

    public void onRoomUpdate(ArrayList<String[]> rooms){
        gui.onRoomUpdate(rooms);
    }

    public void JoinRooms( ArrayList<String> selected){
        model.JoinRooms(selected);
    }

    public void LeaveRooms( ArrayList<String> selected){
        model.LeaveRooms(selected);
    }

    public ArrayList<String> getAllUsers(){
        return model.getAllUsers();
    }

    public ArrayList<String> getUsersFromSelection(ArrayList<String> rooms){
        return model.getAllUsers();
    }

    public void ConnectToAdress(String adress) {
        model.ConnectToAdress(adress);
    }

    public void onConnect(boolean b) {
        gui.onConnect(b);
    }
}
