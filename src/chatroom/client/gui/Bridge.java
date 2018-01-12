package chatroom.client.gui;

import chatroom.client.Client;
import chatroom.client.ClientForTesting;
import chatroom.model.message.LoginResponses;
import javafx.application.Platform;

import java.util.ArrayList;

public class Bridge {
    Client model;
    ClientGuiMain gui;

    public Bridge(Client client, ClientGuiMain clientGuiMain){
        this.model = client;
        this.gui = clientGuiMain;
    }


    //called by the gui to send the login data TODO: ---ERSETZEN------
    public void sendLoginData(String username, String password){
        model.login(username, password);
    }

    //called by the model to let the gui know whether the login succeeded or not TODO: ---BENUTZEN im MODEL------
    public void onServerLoginAnswer(LoginResponses answer){
        Platform.runLater(()->gui.onLoginAnswer(answer));
    }


    //sends the message TODO: ---ERSETZEN------
    public void SendMessage(String message){
        model.sendMessage(message);
    }

    //Adds new Message to the View... Attention!!! The Own message will not be displayed automaticly please add it to the view TODO: ---BENUTZEN im MODEL------
    public void AddMessageToView(String username, String message){
        gui.AddMessage(username, message);
    }

    //gets The username from the ModelTODO: TODO: ---ERSETZEN------
    public String getUsername(){
      return model.getUsername();
    }

    //gets the rooms from the Model TODO: ---ERSETZEN------
    public ArrayList<String> getRooms(){
        return model.getRooms();
    }

    //Updates the room view by adding or removing rooms TODO: ---BENUTZEN im MODEL------
    public void onRoomUpdate(ArrayList<String> rooms){
        gui.onRoomUpdate(rooms);
    }

    //requests all users TODO: ---ERSETZEN------
    public ArrayList<String> getAllUsers(){
        return (ArrayList<String>) model.getAllUsers();
    }

    //requests users from current room TODO: ---ERSETZEN------
    public ArrayList<String> getUsersFromSelection(String room){
        return (ArrayList<String>) model.getAllUsers();
    }

    //connects to the input adress TODO: ---ERSETZEN------
    public void ConnectToAdress(String adress) {
        model.ConnectToAdress(adress);
    }

    //has to be triggered after succesfull connection TODO: ---BENUTZEN im MODEL------
    public void onConnectionAttemtResponse(boolean b) {
        gui.onConnectionAttemtResponse(b);
    }

    //requests room change TODO: ---ERSETZEN------
    public void requestRoomChange(String room) {
        model.requestRoomChange(room);
    }

    //Has to be triggered after succesfull roomchange TODO: ---BENUTZEN im MODEL------
    public void onRoomChangeRequestAccepted(String room){
        gui.onRoomChange(room);
    }

    //updates the all users view TODO: ---BENUTZEN im MODEL------
    public void allUsersUpdate(ArrayList<String> newAllUsers){
        gui.homeGui.allUsersUpdate(newAllUsers);
    }

    //updates current room users view TODO: ---BENUTZEN im MODEL------
    public void userRoomUpdate(ArrayList<String> newCurrentUsers){
       gui.homeGui.userRoomUpdate(newCurrentUsers);
    }
    //this method is for alertboxes. Like kicked message, warning or banned TODO: ---BENUTZEN im MODEL------
    public void issueBox(String message){
        gui.homeGui.showIssueAlert(message);
    }

    //this method is for operations that have to be run befor closing the window TODO: ---ERSETZEN------
    public void runClosinOperations() {

    }
}
