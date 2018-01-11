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

    //sends the message
    public void SendMessage(String message){
        model.sendMessage(message);
    }

    //Adds new Message to the View... Attention!!! The Own message will not be displayed automaticly please add it to the view
    public void AddMessageToView(String username, String message){
        gui.AddMessage(username, message);
    }

    //gets The username from the Model
    public String getUsername(){
      return model.getUsername();
    }

    //gets the rooms from the Model
    public ArrayList<String[]> getRooms(){
        return model.getRooms();
    }

    //Updates the room view by adding or removing rooms
    public void onRoomUpdate(ArrayList<String[]> rooms){
        gui.onRoomUpdate(rooms);
    }

    //requests all users
    public ArrayList<String> getAllUsers(){
        return model.getAllUsers();
    }

    //requests users from current room
    public ArrayList<String> getUsersFromSelection(String room){
        return model.getAllUsers();
    }

    //connects to the input adress
    public void ConnectToAdress(String adress) {
        model.ConnectToAdress(adress);
    }

    //has to be triggered after succesfull connection
    public void onConnect(boolean b) {
        gui.onConnect(b);
    }

    //requests room change
    public void requestRoomChange(String room) {
        gui.onRoomChange(room);
    }

    //updates the all users view
    public void allUsersUpdate(ArrayList<String> newAllUsers){
        gui.homeGui.allUsersUpdate(newAllUsers);
    }

    //updates current room users view
    public void userRoomUpdate(ArrayList<String> newCurrentUsers){
       gui.homeGui.userRoomUpdate(newCurrentUsers);
    }
    //this method is for alertboxes. Like kicked message, warning or banned
    public void issueBox(String message){
        gui.homeGui.showIssueAlert(message);
    }

    //this method is for operations that have to be run befor closing the window
    public void runClosinOperations() {

    }
}
