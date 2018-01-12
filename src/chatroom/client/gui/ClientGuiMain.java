package chatroom.client.gui;


import chatroom.model.message.LoginResponses;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sun.rmi.runtime.Log;

import java.util.ArrayList;

public class ClientGuiMain {

   Bridge bridge;
   Stage window;
   HomeGui homeGui;




   public void runGui(Stage primeStage, Bridge bridge){
       this.window = primeStage;
       this.bridge = bridge;


       window.setTitle("The Chat Client");
       window.setOnCloseRequest(e -> onClose());


       Pane ipBox = ServerBox.ServerInput(bridge, "");
       ipBox.getStylesheets().add(getClass().getResource("HomeGuiStyle.css").toExternalForm());
       Scene scene = new Scene(ipBox);

       window.setScene(scene);
       window.show();

    }
    public void onLoginAnswer(LoginResponses answer){
        switch(answer){
            case SUCCESS:
                LoadHomeWindow(); break;
            case CREATED_ACCOUNT:
                LoadHomeWindow();break;
            case WRONG_PASSWORD: {
                Pane loginBox = Login.LoginBox(bridge, "Wrong Password, or the Username you chose is already forgiven!");
                Scene scene = new Scene(loginBox);
                window.setScene(scene);
                break;
            }
            case ALREADY_LOGGED_IN: {
                Pane loginBox = Login.LoginBox(bridge, "Already Logged in. Please contact the server master!");
                Scene scene = new Scene(loginBox);
                window.setScene(scene);
                break;
            }
        }
    }

    public void LoadHomeWindow(){
        this.homeGui = new HomeGui(bridge, window);
    }

    //TODO: Fix this crap!!!
    public void AddMessage(String username, String message){
            homeGui.addMessage(username, message);
    }

    public void onRoomUpdate(ArrayList<String> rooms){
       homeGui.onRoomUpdate(rooms);
    }


    public void onConnectionAttemtResponse(boolean b) {
       if(b) {
           Pane loginBox = Login.LoginBox(bridge,"");
           loginBox.getStylesheets().add(getClass().getResource("HomeGuiStyle.css").toExternalForm());
           Scene scene = new Scene(loginBox);
           window.setScene(scene);
       }
       else{
           Pane ipBox = ServerBox.ServerInput(bridge, "Server not Available");
           ipBox.getStylesheets().add(getClass().getResource("HomeGuiStyle.css").toExternalForm());
           Scene scene = new Scene(ipBox);
           window.setScene(scene);
       }

    }

    public void onRoomChange(String room) {
       homeGui.onRoomChange(room);
       }

       public void onClose(){
       bridge.runClosinOperations();
    }
}
