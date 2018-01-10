package chatroom.ClientGui;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GuiMain{

   Bridge bridge;
   Stage window;
   HomeGui homeGui;




   public void runGui(Stage primeStage, Bridge bridge){
       this.window = primeStage;
       this.bridge = bridge;


       window.setTitle("The Chat Client");


       Pane ipBox = ServerBox.ServerInput(bridge);
       Scene scene = new Scene(ipBox);

       window.setScene(scene);
       window.show();

    }
    public void onLoginAnswer(boolean answer){
        if(answer)
            LoadHomeWindow();
    }

    public void LoadHomeWindow(){
        this.homeGui = new HomeGui(bridge, window);
    }

    public void AddMessage(String username, String message){
        homeGui.AddMessage(username, message);
    }

    public void onRoomUpdate(ArrayList<String[]> rooms){
       homeGui.onRoomUpdate(rooms);
    }


    public void onConnect(boolean b) {
       if(b) {
           Pane loginBox = Login.LoginBox(bridge);
           Scene scene = new Scene(loginBox);
           window.setScene(scene);
       }

    }
}
